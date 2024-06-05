package Scan;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class VirusScanner {

    private List<String> signatures;
    
    public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            // Tạo một mảng byte để đọc dữ liệu từ file
            byte[] byteArray = new byte[1024];
            int bytesCount;

            // Đọc dữ liệu từ file và cập nhật vào MessageDigest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }

        // Lấy kết quả băm (digest) dưới dạng mảng byte
        byte[] bytes = digest.digest();

        // Chuyển đổi mảng byte thành chuỗi hex
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        // Trả về chuỗi hex đại diện cho mã băm
        return sb.toString();
    }

    public VirusScanner(String signatureFilePath) throws IOException {
        this.signatures = loadSignatures(signatureFilePath);
    }

    private List<String> loadSignatures(String signatureFilePath) throws IOException {
        List<String> signatures = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(signatureFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                signatures.add(line);
            }
        }
        return signatures;
    }

    public void scanFile(String filePath) throws IOException, NoSuchAlgorithmException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        MessageDigest md=MessageDigest.getInstance("MD5");
        File file = new File(filePath);
        String checksum=getFileChecksum(md, file);
        for (String signature : signatures) {
        	if(checksum.contains(signature)) {
        		System.out.println("Malware detected: " + signature + " in file: " + filePath);
        		return;
        	} else {
        		System.out.println(checksum);
        		System.out.println(signature);
        	}
        }
        		
        System.out.println("No malware detected in file: " + filePath);
    }
    
    public void deleteMalware() {
    	
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        if (args.length < 2) {
            System.out.println("Usage: java VirusScanner <signature_file_path> <file_to_scan>");
            return;
        }

        String signatureFilePath = args[0];
        String fileToScan = args[1];

        try {
            VirusScanner scanner = new VirusScanner(signatureFilePath);
            try {
				scanner.scanFile(fileToScan);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
    }
}
