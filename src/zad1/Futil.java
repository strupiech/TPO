package zad1;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.*;

public class Futil {

    public static void processDir(String dirName, String resultFileName) {
        Path startingPath = Paths.get(dirName);
        Path resultPath = Paths.get(resultFileName);
        List<Path> filesPaths = new ArrayList<>();
        try {
            Files.walkFileTree(startingPath, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isRegularFile())
                        filesPaths.add(file);
                    return FileVisitResult.CONTINUE;
                }
            });

            try (FileChannel resultFileChannel = FileChannel.open(resultPath, CREATE, TRUNCATE_EXISTING, WRITE)) {
                for (Path path : filesPaths) {
                    try (FileChannel inputFileChannel = FileChannel.open(path, READ)) {
                        ByteBuffer byteBuffer = ByteBuffer.allocate((int) inputFileChannel.size());
                        inputFileChannel.read(byteBuffer);
                        byteBuffer.flip();
                        CharBuffer decodeBuffer = Charset.forName("Cp1250").decode(byteBuffer);
                        resultFileChannel.write(StandardCharsets.UTF_8.encode(decodeBuffer));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
