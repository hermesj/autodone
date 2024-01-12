package de.uoc.dh.idh.autodone.services;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.univocity.parsers.tsv.TsvParserSettings;

import de.uoc.dh.idh.autodone.entities.MastodonPost;
import de.uoc.dh.idh.autodone.entities.PostGroup;
import de.uoc.dh.idh.autodone.helpers.SimpleTweet;
import de.uoc.dh.idh.autodone.helpers.TSVParser;
import de.uoc.dh.idh.autodone.repositories.PostGroupRepository;
import lombok.extern.log4j.Log4j2;

/**
 * The TsvService parses a tsv-file(tab-seperated-value) to a list of @{@link Facebookpost}.
 */
@Log4j2
@Service
public class TsvService {

    @Autowired
    PostGroupRepository groupRepository;

    @Autowired
    SessionService sessionService;

    @Autowired
    ScheduleService scheduleService;


    /**
     * Parses a tsv-file. Availability check for image url's can be turned on and off.
     * Makes use of the univocity tsv parser
     *
     * @param tsvFile
     * @param id
     * @param imgcheck
     * @return
     * @throws MalformedTsvException
     */
    public List<MastodonPost> parseTSV(File tsvFile, String id, boolean imgcheck, boolean datecheck) throws MalformedTsvException {


        TsvParserSettings settings = new TsvParserSettings();

        //settings.getFormat().setLineSeparator("\n");
        //settings.setMaxCharsPerColumn(2000000);

        TSVParser parser = new TSVParser();

        List<SimpleTweet> allTweets = parser.readTSV(tsvFile);

        List<MastodonPost> parsedPosts = new ArrayList<MastodonPost>();

        int i = 1;
        for (SimpleTweet tweet : allTweets) {
           
            MastodonPost post = simpleTweetToPost(tweet);
            if (post != null) {
                try {
                    if (post.getContent().equals("") && post.getImg().equals("")) {
                        throw new MalformedTsvException("Content Error", i, "no content or image detected");
                    }
                } catch (NullPointerException ne) {
                    throw new MalformedTsvException("Content Error", i, "no content or image detected");
                }
                PostGroup page = groupRepository.findByMstdIdAndMastodonuserId(id,sessionService.getActiveUser().getId());
                post.setGroup(page);
                post.setPageID(page.getFbId());
                post.setEnabled(true);
                post.setMastodonuser(sessionService.getActiveUser());
                if(post.getImg()==null){
                    post.setImg("");
                }
                parsedPosts.add(post);
                i++;
            } else throw new MalformedTsvException("Unspecific Formatting Error", i, Arrays.asList(tweet).toString());
        }


        return parsedPosts;
    }


    /**
     * Iterates over each value in a row and creates a @{@link Facebookpost}
     * Tsv Error handling is done here.
     */
    private MastodonPost simpleTweetToPost(SimpleTweet tweet) throws MalformedTsvException {

    	MastodonPost post = new MastodonPost(null, null, null, null);
    	post.setDate(tweet.getDate().toString());
    	post.setTime(tweet.getTime().toString());
    	post.setContent(tweet.getText());
    	String imgvalue = tweet.getUrl().toString();
    	
    	// TODO no break here!
    	//if (!isImage(imgvalue)) throw new MalformedTsvException("Image Error", 0, imgvalue);
    	
    	if(imgvalue==null){
    		post.setImg("");
    	}
    	else{
    		post.setImg(imgvalue);
    	}
        return post;
    }


    /**
     * Checks if the image URL is reachable. Can be turned on and off in @parseTSV
     *
     * @param imagePath
     * @return
     */
    private boolean isImage(String imagePath) {
        try {
            URL url = new URL(imagePath);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            int status = con.getResponseCode();

            if (status != 200) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
