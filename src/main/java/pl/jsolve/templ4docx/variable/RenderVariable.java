package pl.jsolve.templ4docx.variable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class RenderVariable implements Variable {

    private final String key;
    private final List<Object> values;

    public RenderVariable(String key)
    {
        this.key = key;
        this.values = new ArrayList<>();
    }

    public String getKey()
    {
        return key;
    }

    public List<Object> getValues() {
        return values;
    }

    public RenderVariable addParagraph(String text){
        Paragraph paragraph = new Paragraph(Objects.isNull(text) ? "" : text);
        values.add(paragraph);
        return this;
    }
    public RenderVariable addTable(String[][] rows){
        if (ArrayUtils.isEmpty(rows)) {
            return this;
        }
        List<List<String>> rowList = new ArrayList<>();

        int size = rows[0].length;
        for (String[] row : rows) {
            // 校验rowsAndcells里边每一行的列数应该相同
            if (row.length != size){
                throw new IllegalArgumentException("rowsAndcells should be a 2D array, and every row should have the same number of columns");
            }

            ArrayList<String> cellList = new ArrayList<>();
            for (String cell : row) {
                cellList.add(cell);
            }
            rowList.add(cellList);
        }
        Table table = new Table(rowList);
        values.add(table);
        return this;
    }

    public class Paragraph {

        public Paragraph(String text) {
            this.text = text;
        }

        private String text;

        public String getText() {
            return text;
        }
    }

    public class Table {

        public Table(List<List<String>> rowsAndCells) {
            this.rowsAndCells = rowsAndCells;
        }

        private List<List<String>> rowsAndCells;

        public List<List<String>> getRowsAndCells() {
            return rowsAndCells;
        }
    }

}
