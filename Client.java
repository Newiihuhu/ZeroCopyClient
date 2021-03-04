
import java.nio.channels.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";
    public static SocketChannel socket;
    public static ByteBuffer buf;
    public static FileChannel fileChannel;
    public static final String destpath = "//home//newii//NetBeansProjects//ZeroCopyClient//files";

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner in = new Scanner(System.in);

        socket = SocketChannel.open();
        socket.connect(new InetSocketAddress("172.20.40.241", 1234));
        System.out.println(GREEN + "Connection Success!!!");
        System.out.println(GREEN + "Choose Filename to transfer : ");
        String sourcename = in.next();
        sentData(sourcename);
        System.out.println(GREEN + "create new 'Filename' to transfer : ");
        String dest = in.next();
        sentData(dest);
        receiveFile(sourcename, dest);

    }

    public static void sentData(String data) throws UnsupportedEncodingException, IOException {
        buf = ByteBuffer.allocate(1024);
        buf.clear();
        buf.put(data.getBytes("UTF-8"));
        buf.flip();

        while (buf.hasRemaining()) {
            socket.write(buf);
        }
    }

    public static void receiveFile(String source, String dest) throws FileNotFoundException, IOException {
        //System.out.println("start receive");

        String path = destpath + "//" + dest;
        fileChannel = new FileOutputStream(path).getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(8);

        socket.read(buffer);
        buffer.flip();
        long size = buffer.getLong();
        buffer.clear();
        Long start = System.currentTimeMillis();
        long total = 0;
        int i = 0;
        while (total < size) {
            long tranferFromCount = 0;
            if (total == (size / 100) * 99) {
                tranferFromCount = fileChannel.transferFrom(socket, total, (size / 100) + size % 100);
            } else {
                tranferFromCount = fileChannel.transferFrom(socket, total, size / 100);
            }
            if (tranferFromCount <= 0) {
                break;
            }
            total += tranferFromCount;

            if (i <= 101) {
                System.out.println("loading... "+i + "%");
                i++;
            }

        }

        System.out.println("Send File Success");
        System.out.println("Size of file is : " + size);
        System.out.println("Use Time : " + status(total, size, start));
        System.out.println("Transfer File Success");

    }

    public static String status(long now, long max, long start) {
        long sum = (now * 100) / max;
        long time = (System.currentTimeMillis() / 1000) - start / 1000;
        String a = sum + "% " + Long.toString(time) + "sec ";
        for (int i = 0; i < sum; i++) {
            if (i % 3 == 0) {
                a += "";
            }
        }
        Long min = time / 60;
        Long sec = time - (min * 60);
        return min + " min " + sec + " sec";
    }

}