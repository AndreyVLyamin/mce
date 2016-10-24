package dlc.template;

//import org.dom4j.*;
//import org.dom4j.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.io.InputStream;

/**
 * $Id: TemplateFactory.java,v 1 2007/02/20
 * <br/>
 * Author: Вашенков О.Е.
 * <br/>
 * Класс, фабрику стилей кодирования. Информация о стилях кодирования читается из внешнего XML-файла конфигурации
 */
public interface TemplateFactory {


    /**
     * Получить массив ключевых слов
     * @return массив ключевых слов
     */
    public Vector getKeywords();

    /**
     * Получить список выражений подстановки (для If, While, DoWhile)
     */
    public Vector getPlaceholders();

    /**
     * Получить список выражений подстановки (для If, While, DoWhile)
     */
    public Vector getAutoComplete();

    /**
     * Получить список стилей кодирования
     */
    public String[] getStyles();

    /**
     * Получение имени стиля (на русском языке) по псевдониму стиля
     * (псевдоним указывается в атрибуте CodeStyle/@nameAlias)
     * @param aliasStyle название псевдонима
     * @return название стиля
     */
    public String getStyleByAlias( String aliasStyle );

    /**
     * Получение текущего псевдонима стиля (для передачи по сети)
     * @return псевдоним текущего стиля
     */
    public String getCurrentStyleAlias();

    /**
     * Переключение стиля и переинициализация шаблонов
     * на основе стиля style
     */
    public boolean switchStyle( String style );

    /**
     * Получить шаблон (для текущего стиля)
     */
    public IfTemplate getIfTemplate();

    /**
     * Получить шаблон (для текущего стиля)
     */
    public WhileTemplate getWhileTemplate();

    /**
     * Получить шаблон (для текущего стиля)
     */
    public DoWhileTemplate getDoWhileTemplate();

    /**
     * Получить шаблон (для текущего стиля)
     */
    public ExpressionTemplate getExpressionTemplate();

    /**
     * Получить текущий стиль
     */
    public String getCurrentStyle();

    /**
     * Является ли выбранный стиль свободным?
     */
    public boolean isFree();
}
