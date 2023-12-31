package de.uoc.dh.idh.autodone.services;

/**
 * This Exception is thrown if the @{@link TsvService} encounters a parsing error
 */
public class MalformedTsvException extends Exception {

    private int row;
    private String content;


    public  MalformedTsvException(String errorMessage, int row, String content){
        super(errorMessage);
        this.row = row;
        this.content = content;
    }

    public int getRow() {
        return row;
    }

    public String getContent() {
        return content;
    }
}
