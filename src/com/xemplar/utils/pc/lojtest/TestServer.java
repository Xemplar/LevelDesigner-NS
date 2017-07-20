package com.xemplar.utils.pc.lojtest;

import com.xemplar.utils.pc.leveldesigner.Main;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Rohan on 7/12/2017.
 */
public class TestServer extends JFrame implements Runnable {
    public static final String TEST_RESPONSE = "LOJNS-34953-BHJKB-34850-JNKJN Response";
    public static final String TEST_REQUEST = "LOJNS-34953-BHJKB-34850-JNKJN Request";

    public static final int TEST_PORT_DISCOVER = 57638;
    public static final int TEST_PORT_COMM = 57639;

    public static String levelData = "";

    public static void main(String[] args){
        File f;

        if(args.length == 0){
            f = new File(System.getProperty("user.dir") + "/levelExp.txt");

            if(!f.exists()) {
                System.out.println("Usage: java -jar TestServer.jar <level_file>");
                System.exit(0);
            }
        } else {
            f = new File(System.getProperty("user.dir") + args[0]);
        }

        try {
            BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

            String line;
            while((line = file.readLine()) != null){
                levelData += line + "=";
            }
            levelData = levelData.substring(0, levelData.length() - 1);
            Main.instance.status.setServerStatus(true);
        } catch(FileNotFoundException e){
            System.out.println("That file does not exist.");
        } catch(Exception e){
            System.out.println("An unknown error occurred, please contact the developer at service@xemplarsoft.com with the stacktrace below:");
            e.printStackTrace();
        }


        Thread testServ = new Thread(new Runnable() {
            public void run(){
                DatagramSocket c;
                try {
                    //Keep a socket open to listen to all the UDP traffic that is destined for this port
                    c = new DatagramSocket(TEST_PORT_DISCOVER, InetAddress.getByName("0.0.0.0"));
                    c.setBroadcast(true);
                    boolean connected = false;
                    while (!connected) {
                        System.out.println("5:Ready to receive broadcast packets!");

                        //Receive a packet
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        c.receive(packet);

                        //Packet received
                        System.out.println("6:Discovery packet received from: " + packet.getAddress().getHostAddress());
                        System.out.println("7:Packet received; data: " + new String(packet.getData()));

                        //See if the packet holds the right command (message)
                        String message = new String(packet.getData()).trim();
                        if (message.equals(TEST_REQUEST)) {
                            byte[] sendData = TEST_RESPONSE.getBytes();

                            //Send a response
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                            c.send(sendPacket);

                            System.out.println("8:Sent packet to: " + sendPacket.getAddress().getHostAddress());

                            connected = true;
                            TestServerHolder.INSTANCE.startServer();
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        testServ.start();
    }

    public void startServer(){
        System.out.println("0:Server Started");
        Thread t = new Thread(this);
        t.start();
    }

    public void run(){
        ServerSocket socket;
        Socket loj;
        try{
            socket = new ServerSocket(TEST_PORT_COMM);
            loj = socket.accept();
            System.out.println("1:Client Connected");

            BufferedReader reader = new BufferedReader(new InputStreamReader(loj.getInputStream()));
            boolean ready = false;
            while(!ready){
                ready = reader.readLine().equals("ready");
            }
            System.out.println("2:Client is Ready");

            PrintWriter writer = new PrintWriter(loj.getOutputStream(), true);
            writer.write("99;ready\n");
            writer.flush();

            //String data = "1;" + new String(toSHA1(levelData.getBytes()))+ "\n";
            //writer.write(data);
            //writer.flush();

            writer.write("2;" + levelData + "\n");
            System.out.println("3:levelData: " + levelData);
            writer.flush();

            reader.close();
            writer.close();
            loj.close();
            socket.close();

            Main.instance.status.setServerStatus(false);
        } catch(Exception e){
            System.out.println("-1:An unknown error has occurred, please contact the developer ar service@xemplarsoft.com with the stacktrace below:");
            e.printStackTrace();
        }
    }

    public static byte[] toSHA1(byte[] base) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md.digest(base);
    }

    private static class TestServerHolder {
        private static final TestServer INSTANCE = new TestServer();
    }
}
