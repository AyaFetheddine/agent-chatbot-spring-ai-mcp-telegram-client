package ma.fetheddine.mcpserver.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class DocumentIndexor {
    @Value("classpath:/pdfs/cv.pdf")
    private Resource pdfResource;
    @Value("store.json")
    private String storeFileName;
    @Bean
    public SimpleVectorStore getVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        Path path = Paths.get("mcp-server","src","main","resources","store");
        File file = new File(path.toFile(), storeFileName);
        if(!file.exists()) {
            PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource);
            List<Document> documents = reader.get();
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.apply(documents);
            vectorStore.add(chunks);
            vectorStore.save(file);
        } else {
            vectorStore.load(file);
        }
        return vectorStore;
    }
}