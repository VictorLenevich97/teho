package va.rit.teho.service;

import java.io.*;
import java.util.List;

public class DataExporter {

    private static final byte[] EMPTY_RESPONSE = new byte[]{};

    public static <T> byte[] exportData(List<T> data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(data);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EMPTY_RESPONSE;
    }

    public static <T> T importData(byte[] byteArray) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
