// OUT OF ORDER ON ANDROID
// uh. no java.nio.file package on Android.
// have to reimplement that stuff with Android's proper SDK code instead.

//package pw.veselinromic.microsocial.poc;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.ArrayList;
//import java.util.List;
//
//public class FileHandler
//{
//    static List<String> readAllLines (String filePath)
//    {
//        // check if the file exists
//        File file = new File(filePath);
//        if (file.exists() && !file.isDirectory())
//        {
//            // file exists
//            try
//            {
//                return Files.readAllLines(Paths.get(filePath), Charset.forName("UTF-8"));
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//                return null;
//            }
//        }
//        else
//        {
//            // file doesn't exist
//            return null;
//        }
//    }
//
//    static void writeLines(String filePath, List<String> lines)
//    {
//        // check if the file exists
//        File file = new File(filePath);
//        if (file.exists() && !file.isDirectory())
//        {
//            // file exists
//            try
//            {
//                Path path = Paths.get(filePath);
//
//                Files.write(path, lines, Charset.forName("UTF-8"),
//                        StandardOpenOption.WRITE, StandardOpenOption.APPEND);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
//            // file doesn't exist, create it before writing
//
//            try
//            {
//                Path path = Paths.get(filePath);
//
//                Files.write(path, lines, Charset.forName("UTF-8"),
//                        StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE_NEW);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    static void writeLine(String filePath, String line)
//    {
//        List<String> lines = new ArrayList<String>();
//        lines.add(line);
//
//        // check if the file exists
//        File file = new File(filePath);
//        if (file.exists() && !file.isDirectory())
//        {
//            // file exists
//            try
//            {
//                Path path = Paths.get(filePath);
//
//                Files.write(path, lines, Charset.forName("UTF-8"),
//                        StandardOpenOption.WRITE, StandardOpenOption.APPEND);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
//            // file doesn't exist, create it before writing
//
//            try
//            {
//                Path path = Paths.get(filePath);
//
//                Files.write(path, lines, Charset.forName("UTF-8"),
//                        StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE_NEW);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    static boolean fileExists(String path)
//    {
//        File file = new File(path);
//
//        if (file.exists() && !file.isDirectory()) return true;
//        else return false;
//    }
//}
