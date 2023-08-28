package de.uoc.dh.idh.autodone.services;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import de.uoc.dh.idh.autodone.entities.MastodonPost;
import de.uoc.dh.idh.autodone.entities.PostGroup;
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

        settings.getFormat().setLineSeparator("\n");
        settings.setMaxCharsPerColumn(2000000);

        TsvParser parser = new TsvParser(settings);

        List<String[]> allRows = parser.parseAll(tsvFile);

        List<MastodonPost> parsedPosts = new ArrayList<MastodonPost>();

        int i = 1;
        for (String[] row : allRows) {
            //Ignore lines with '//'
            if (row[0].startsWith("//")) {
                //log.info("Comment in line " + i);
                continue;
            }

            MastodonPost post = arrayToPost(row, i, imgcheck, datecheck);
            if (post != null) {
                try {
                    if (post.getContent().equals("") && post.getImg().equals("")) {
                        throw new MalformedTsvException("Content Error", i, "no content or image detected");
                    }
                } catch (NullPointerException ne) {
                    throw new MalformedTsvException("Content Error", i, "no content or image detected");
                }
                PostGroup page = groupRepository.findByFbIdAndFacebookuser_Id(id,sessionService.getActiveUser().getId());
                post.setGroup(page);
                post.setPageID(page.getFbId());
                post.setEnabled(true);
                post.setMastodonuser(sessionService.getActiveUser());
                if(post.getImg()==null){
                    post.setImg("");
                }
                parsedPosts.add(post);
                i++;
            } else throw new MalformedTsvException("Unspecific Formatting Error", i, Arrays.asList(row).toString());
        }


        return parsedPosts;
    }


    /**
     * Iterates over each value in a row and creates a @{@link Facebookpost}
     * Tsv Error handling is done here.
     */
    private MastodonPost arrayToPost(String[] posts, int row, boolean imgcheck, boolean datecheck) throws MalformedTsvException {
//TODO Fill Constructor!
    	MastodonPost post = new MastodonPost(null, null, null, null);
        for (int col = 0; col < posts.length; col++) {
            String value = posts[col];
            String originalDate = "";
            switch (col) {
                case 0:
                    //Date
                    try {
                        originalDate = value;
                        String date = DateParser.parse(value,datecheck);
                        post.setDate(date);
                        if (date == null) {
                            throw new MalformedTsvException("Date Error", row, value);
                        }
                    } catch (Exception e) {
                        throw new MalformedTsvException("Date Error", row, value);
                    }
                    break;
                case 1:
                    //Time
                    try {
                        if(value.split(":").length == 3){
                            String[] timeArray = value.split(":");
                            value = timeArray[0] + ":" + timeArray[1];
                        }
                        post.setTime(value);
                        int delay = scheduleService.getDelay(post);
                        if (delay < 0 && datecheck) {
                            throw new MalformedTsvException("Time Error", row, originalDate + " " + value);
                        } else if (delay < 0) {
                            post.setError(true);
                        }

                    } catch (Exception e) {
                        throw new MalformedTsvException("Time Error", row, originalDate + " " + value);
                    }
                    break;
                case 2:
                    //Content
                    post.setContent(value != null ? value : "");
                    break;
                case 3:
                    //Image
                    if (imgcheck) {
                        if (!isImage(value)) throw new MalformedTsvException("Image Error", row, value);
                    }
                    if(value==null){
                        post.setImg("");
                    }else{
                        post.setImg(value);
                    }

                    break;
                default:
                    break;

            }
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
