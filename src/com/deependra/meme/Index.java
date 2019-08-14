package com.deependra.meme;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.deependra.meme.Data.Meme;
import com.deependra.meme.Data.MemeData;
import com.google.gson.Gson;

public class Index {
    private static final Gson GSON = new Gson();
    private final MemeData memesData;
    private final Directory index;
    private final StandardAnalyzer analyzer;

    public Index() throws IOException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        analyzer = new StandardAnalyzer(Version.LUCENE_40);

        // 1. create the index
        index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);

        IndexWriter w = new IndexWriter(index, config);

        BufferedReader bufferedReader = new BufferedReader(new FileReader("./resources/db.json"));
        memesData = GSON.fromJson(bufferedReader, MemeData.class);
        createIndex(w, memesData);
    }

    public String searchIndex(String input) throws IOException {
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = null;
        try {
            q = new QueryParser(Version.LUCENE_40, "title", analyzer).parse(input);
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
        }

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + memesData.get_default().get(Long.valueOf(d.get("index"))));
        }
        return Stream.of(hits).map(Objects::toString).collect(Collectors.joining(","));
    }

    private static void createIndex(IndexWriter w, MemeData memeData) throws IOException {
        for (Map.Entry<Long, Meme> entry : memeData.get_default().entrySet()) {
            addDoc(w, entry.getValue().getTitle(), entry.getKey());
        }
        w.close();
    }

    private static void addDoc(IndexWriter w, String title, Long index) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new LongField("index", index, Field.Store.YES));
        w.addDocument(doc);
    }
}
