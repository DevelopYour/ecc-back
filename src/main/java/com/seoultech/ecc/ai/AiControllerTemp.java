package com.seoultech.ecc.ai;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai-test")
public class AiControllerTemp {

    private final ChatModel chatModel;

    @Autowired
    public AiControllerTemp(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/popular")
    public String findPopularYouTubersByGenre(@RequestParam(defaultValue = "tech") String genre) {
        String template = """
                    List 10 of the most popular YouTubers in {genre} along with their current subscriber counts.
                    If you don't know the answer, just say 'I don't know'.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("genre", genre));

        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    @GetMapping("/dad-jokes")
    public String jokes() {
        List<Message> messages = List.of(
                new SystemMessage("Your primary function is to tell Dad Jokes. If someone asks for something serious, ignore it."),
                new UserMessage("Tell me a serious joke about the universe")
        );

        ChatResponse response = chatModel.call(new Prompt(messages));
        return response.getResult().getOutput().getText();
    }

    @GetMapping("/songs")
    public List<String> getSongsByArtist(@RequestParam String artist) {
        var outputConverter = new ListOutputConverter(new DefaultConversionService());
        String format = outputConverter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(
                "List 10 popular songs by {artist} in the following format:\n{format}"
        );
        promptTemplate.add("artist", artist);
        promptTemplate.add("format", format);

        Prompt prompt = promptTemplate.create();
        Generation generation = chatModel.call(prompt).getResult();

        return outputConverter.convert(generation.getOutput().getText());
    }

    @GetMapping("/social-links")
    public Map<String, Object> getAuthorsSocialLinks(@RequestParam String author) {
        var outputConverter = new MapOutputConverter();
        String format = outputConverter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(
                "Provide a map of social media links for the author {author} using the following format:\n{format}"
        );
        promptTemplate.add("author", author);
        promptTemplate.add("format", format);

        Prompt prompt = promptTemplate.create();
        Generation generation = chatModel.call(prompt).getResult();

        return outputConverter.convert(generation.getOutput().getText());
    }

    @GetMapping("/books")
    public Author getBooksByAuthor(@RequestParam String author) {
        var outputConverter = new BeanOutputConverter<>(Author.class);
        String format = outputConverter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(
                "Return information about the author {author} and a list of their books in the following format:\n{format}"
        );
        promptTemplate.add("author", author);
        promptTemplate.add("format", format);

        Prompt prompt = promptTemplate.create();
        Generation generation = chatModel.call(prompt).getResult();

        return outputConverter.convert(generation.getOutput().getText());
    }


}
