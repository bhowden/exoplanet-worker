package com.bhowden.exoplanetworker.services;

import com.google.gson.Gson;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
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

    public void processExoplanets(List<Exoplanet> exoplanets) {
        Session session = null;
        ChannelExec channel = null;

        try {
            JSch jsch = new JSch();
            jsch.addIdentity(privateKeyPath);

            session = jsch.getSession(username, hostname, port);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no"); // Disable host key checking
            session.setConfig(config);
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");

            for (int i = 0; i < exoplanets.size(); i++) {
                Exoplanet exoplanet = exoplanets.get(i);
                boolean keepAlive = (exoplanet.getStayAlive() == 1) && (i < exoplanets.size() - 1);

                String command = createSshCommand(exoplanet, keepAlive); // Create the SSH command
                channel.setCommand(command);

                InputStream in = channel.getInputStream();
                channel.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Deserialize the JSON response into an Exoplanet object
                Gson gson = new Gson();
                Exoplanet updatedExoplanet = gson.fromJson(result.toString(), Exoplanet.class);

                exoplanet = updatedExoplanet;

                if (!keepAlive) {
                    channel.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    private String createSshCommand(Exoplanet exoplanet, boolean keepAlive) {
        String jsonPayload = createJsonString(exoplanet);
        return "echo '" + jsonPayload + "' | ssh -p " + port + (keepAlive ? " -o ServerAliveInterval=60 -o ServerAliveCountMax=3" : "") + " -i " + privateKeyPath + " " + username + "@" + hostname;
    }

    private String createJsonString(Exoplanet exoplanet) {
        Gson gson = new Gson();
        return gson.toJson(exoplanet);
    }
}
