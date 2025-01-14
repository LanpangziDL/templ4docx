package pl.jsolve.templ4docx.strategy;

import pl.jsolve.templ4docx.cleaner.ParagraphCleaner;
import pl.jsolve.templ4docx.cleaner.TableRowCleaner;
import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.variable.Variable;
import pl.jsolve.templ4docx.variable.Variables;

public class InsertStrategyChooser {

    private TextInsertStrategy textInsertStrategy;
    private ImageInsertStrategy imageInsertStrategy;
    private TableInsertStrategy tableInsertStrategy;
    private BulletListInsertStrategy bulletListInsertStrategy;
    private ObjectInsertStrategy objectInsertStrategy;
    private RenderInsertStrategy renderInsertStrategy;
    private Variables variables;

    public InsertStrategyChooser(Variables variables, TableRowCleaner tableRowCleaner, ParagraphCleaner paragraphCleaner) {
        this.textInsertStrategy = new TextInsertStrategy();
        this.imageInsertStrategy = new ImageInsertStrategy();
        this.tableInsertStrategy = new TableInsertStrategy(variables, this, tableRowCleaner);
        this.bulletListInsertStrategy = new BulletListInsertStrategy(this, paragraphCleaner);
        this.objectInsertStrategy = new ObjectInsertStrategy();
        this.renderInsertStrategy = new RenderInsertStrategy();
        this.variables = variables;
    }

    public void replace(Insert insert, Variable variable) {
        switch (insert.getKey().getVariableType()) {
        case TEXT:
            textInsertStrategy.insert(insert, variable);
            break;
        case IMAGE:
            imageInsertStrategy.insert(insert, variable);
            break;
        case TABLE:
            tableInsertStrategy.insert(insert, variable);
            break;
        case BULLET_LIST:
            bulletListInsertStrategy.insert(insert, variable);
            break;
        case OBJECT:
            objectInsertStrategy.insert(insert, variable);
            break;
        case RENDER:
            renderInsertStrategy.insert(insert, variable);
            break;
        }
    }

    public void replace(Insert insert) {
        replace(insert, variables.getVariable(insert.getKey()));
    }

    public void cleanUp() {
        tableInsertStrategy.cleanRows();
        bulletListInsertStrategy.cleanParagraphs();
    }
}
