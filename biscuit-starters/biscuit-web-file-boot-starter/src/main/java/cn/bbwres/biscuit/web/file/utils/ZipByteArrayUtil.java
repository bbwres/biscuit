package cn.bbwres.biscuit.web.file.utils;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;

/**
 * 压缩多个文件的byte数据为ZIP，并返回输入流
 *
 * @author zlf
 */
public class ZipByteArrayUtil {

    /**
     * 压缩多个文件的byte数据为ZIP，并返回输入流
     *
     * @param fileDataMap 键：文件名（含扩展名），值：文件的byte数组
     * @return 压缩后的ZIP输入流
     */
    public static void compressToZipStream(OutputStream outputStream, Map<String, byte[]> fileDataMap) {
        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            // 3. 遍历所有文件数据，写入ZIP
            for (Map.Entry<String, byte[]> entry : fileDataMap.entrySet()) {
                String fileName = entry.getKey();
                byte[] fileData = entry.getValue();

                // 3.1 创建ZIP条目（代表一个文件）
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);

                // 3.2 写入文件byte数据
                zipOut.write(fileData);
                // 3.3 关闭当前条目
                zipOut.closeEntry();
            }
            zipOut.flush();
        } catch (Exception e) {
            throw new RuntimeException("文件压缩异常");
        }
    }


    /**
     * 压缩多个InputStream到同一个ZIP文件
     *
     * @param streamEntries 待压缩的流列表（包含流和对应文件名）
     * @param outputStream  zip 输出流
     * @throws IOException
     */
    public static void compressMultipleInputStreamsToZip(List<StreamEntry> streamEntries, OutputStream outputStream) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(outputStream);
             ZipOutputStream zipOut = new ZipOutputStream(bos)) {
            for (StreamEntry entry : streamEntries) {
                try (BufferedInputStream bis = new BufferedInputStream(entry.inputStream())) {
                    // 创建ZIP条目（每个流对应一个文件名）
                    ZipEntry zipEntry = new ZipEntry(entry.entryName());
                    zipOut.putNextEntry(zipEntry);
                    // 逐块读取并压缩
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, bytesRead);
                    }
                    zipOut.closeEntry();
                } finally {
                    // 关闭当前InputStream（避免资源泄漏）
                    entry.inputStream().close();
                }
            }
        }
    }

    // 封装InputStream和对应文件名的实体类
    public record StreamEntry(InputStream inputStream, String entryName) {

    }

}
