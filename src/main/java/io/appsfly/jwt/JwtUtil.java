package io.appsfly.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.appsfly.util.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JwtUtil {
    public static void main(String[] args) {
        JwtUtil jwtUtil = new JwtUtil();
        String data = new JSONObject() {{
            this.put("find", "srikanth");
        }}.toString();
        System.out.println("data is " + data);
        String mohit = jwtUtil.generateChecksum(data, "123");
        System.out.println("token for the above keys is " + mohit);
        String data1 = new JSONObject() {{
            this.put("find", "srikanth");
        }}.toString();
        boolean b = jwtUtil.verifyCheckSum(mohit, "123", data1);
        System.out.println("data validation is " + b);
    }

    public String generateChecksum(String data, String secret) {
        String token = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            token = JWT.create()
                    .withClaim("claim", data)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException exception) {
            //UTF-8 encoding not supported
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return token;

    }

    public boolean verifyCheckSum(String token, String secret, String data) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("claim", data)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            System.out.print("verification process data is " + jwt.getPayload());
            DecodedJWT jwt1 = JWT.decode(token);
            System.out.println("decoded token  process data is " + jwt1.getPayload());
            Claim claim = jwt1.getClaim("claim");
            String s = claim.asString();
            System.out.println("data from jwt is " + s);
            return true;
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
            //UTF-8 encoding not supported
            return false;
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            exception.printStackTrace();
            return false;
        }
    }

}
