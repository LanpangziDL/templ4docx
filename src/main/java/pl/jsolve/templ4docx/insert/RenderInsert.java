package pl.jsolve.templ4docx.insert;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import pl.jsolve.templ4docx.util.Key;

/**
 * Insert for render variable.
 */
public class RenderInsert extends Insert {

    /**
     * Paragraph which contains render variable
     */
    private XWPFParagraph paragraph;

    public RenderInsert(Key key, XWPFParagraph paragraph) {
        super(key);
        this.paragraph = paragraph;
    }

    public XWPFParagraph getParagraph() {
        return paragraph;
    }

}