import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ValuableVocabularyGUI extends JFrame {
    private JPanel mainPanel;
    private JLabel wordLabel;
    private JButton refreshButton;
    private JLabel partOfSpeechLabel;
    private JLabel definitionLabel;
    private static String theWord = "";
    private static String thePartOfSpeech = "";
    private static String theDefinition = "";

    public ValuableVocabularyGUI(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // wordLabel.setText();
                String apiKey = "";
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.wordnik.com/v4/words.json/randomWord?hasDictionaryDef=true&maxCorpusCount=-1&minDictionaryCount=1&maxDictionaryCount=-1&minLength=5&maxLength=-1&api_key=" + apiKey)).build();
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenAccept(ValuableVocabularyGUI::parse1)
                        .join();
                if(theWord.equals("No word found")) {
                    System.exit(0);
                }
                HttpRequest request1 = HttpRequest.newBuilder(URI.create("https://api.wordnik.com/v4/word.json/" + theWord + "/definitions?limit=1&includeRelated=false&sourceDictionaries=webster&useCanonical=false&includeTags=false&api_key=" + apiKey)).build();
                client.sendAsync(request1, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenAccept(ValuableVocabularyGUI::parse2)
                        .join();

                wordLabel.setText("word: " + theWord);
                partOfSpeechLabel.setText("part of speech: " + thePartOfSpeech);
                definitionLabel.setText("definition: " + theDefinition);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new ValuableVocabularyGUI("Valuable Vocabulary");
        frame.setVisible(true);
    }

    public static String parse1(String responseBody)
    {
        try
        {
            JSONObject word = new JSONObject(responseBody);
            theWord = word.getString("word");
            return null;
        }
        catch (JSONException e)
        {
            theWord = "No word found";
        }
        return null;

    }

    public static String parse2(String responseBody)
    {
        try
        {
            JSONArray albums = new JSONArray(responseBody);
            JSONObject album = albums.getJSONObject(0);
            thePartOfSpeech = album.getString("partOfSpeech");
            theDefinition = album.getString("text");
            return null;
        }
        catch (JSONException e)
        {
            thePartOfSpeech = "No part of speech found";
            theDefinition = "No definition found";
        }
        return null;
    }

}
