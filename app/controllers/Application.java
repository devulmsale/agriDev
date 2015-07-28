package controllers;

/*import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;*/
import play.mvc.*;

import java.io.*;
import java.nio.file.Paths;

/*import static org.apache.lucene.document.Field.*;*/

public class Application extends Controller {

   /* public static void index() {
        //指定索引文件夹的位置
         *//* 指明要索引文件夹的位置,这里是C盘的source文件夹下 *//*
        File fileDir = new File("/Users/upshan/opt/lucenc");
        String indexPath = "/Users/upshan/opt/lucenc/score";
        // 创建 Directory
//        Directory directory = new RAMDirectory();  // 索引简历在内存中
        Directory directory = null;
        try {
            directory = FSDirectory.open(Paths.get(indexPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Analyzer luceneAnalyzer = new StandardAnalyzer();
        // 创建 IndexWriter
        IndexWriterConfig indexConf = new IndexWriterConfig(luceneAnalyzer);
        IndexWriter indexWriter = null;
        try {
            indexWriter  =  new IndexWriter(directory , indexConf);
            for(File file : fileDir.listFiles()) {
                Document doc = new Document();
                String temp = fileReaderAll(file.getCanonicalPath(),
                        "GBK");
                Field FieldPath = new StringField("path", file.getPath(), Store.YES);
                Field FieldBody = new TextField("body" , temp , Store.YES);
                doc.add(FieldPath);
                doc.add(FieldBody);
                indexWriter.addDocument(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        render();
    }*/

    public static String fileReaderAll(String FileName, String charset)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(FileName), charset));
        String line = new String();
        String temp = new String();

        while ((line = reader.readLine()) != null) {
            temp += line;
        }
        reader.close();
        return temp;
    }

}