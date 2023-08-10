package com.bhowden.exoplanetworker.services;

import com.google.gson.Gson;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bhowden.exoplanetworker.model.Exoplanet;

@Service
public class SshClientService {  

    @Value("${ssh.server.hostname}")
    private String hostname;

    @Value("${ssh.server.port}")
    private int port;

    @Value("${ssh.server.username}")
    private String username;

    @Value("${ssh.server.private-key-path}")
    private String privateKeyPath;

    public Exoplanet get(Exoplanet exoplanet) {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(privateKeyPath);

            Session session = jsch.getSession(username, hostname, port);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no"); // Disable host key checking
            session.setConfig(config);
            session.connect();

            String command = createSshCommand(exoplanet); // Create the SSH command

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            InputStream in = channel.getInputStream();
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            channel.disconnect();
            session.disconnect();

            // Deserialize the JSON response into an Exoplanet object
            Gson gson = new Gson();
            return gson.fromJson(result.toString(), Exoplanet.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String createSshCommand(Exoplanet exoplanet) {
        String jsonPayload = createJsonString(exoplanet);
        return "echo '" + jsonPayload + "' | ssh -p " + port + " -i " + privateKeyPath + " " + username + "@" + hostname;
    }

    private String createJsonString(Exoplanet exoplanet) {
        Gson gson = new Gson();
        return gson.toJson(exoplanet);
    }

}
