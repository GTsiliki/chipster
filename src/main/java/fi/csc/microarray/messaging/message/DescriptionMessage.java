package fi.csc.microarray.messaging.message;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fi.csc.microarray.util.XmlUtil;

/**
 * Message for sending module information and tool
 * descriptions.
 * 
 * @author naktinis
 *
 */
public class DescriptionMessage extends ChipsterMessage {
    
    private final static String KEY_MODULE = "module";
    private final static String KEY_MODULE_NAME = "module-name";
    
    public Document moduleXml;
    private String moduleName;
    private List<Category> categories = new LinkedList<Category>();
    
    /**
     * Empty constructor (needed for MessageListenerWrap.onMessage)
     */
    public DescriptionMessage() { }
    
    public DescriptionMessage(String moduleName) throws ParserConfigurationException {
        // Start constructing the XML
        moduleXml = XmlUtil.newDocument();
        moduleXml.appendChild(moduleXml.createElement("module"));
        
        // Set module's name
        setModuleName(moduleName);
        getFirstModule().setAttribute("name", moduleName);
    }
    
    private void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public String getModuleName() {
        return this.moduleName;
    }
    
    public void addConfString(String configuration) {
        // TODO ADD to XML
    }
    
    private Element getFirstModule() {
        return (Element)moduleXml.getElementsByTagName("module").item(0);
    }
    
    /**
     * Store a Category object in the inner XML.
     * 
     * @param category
     */
    public void addCategory(Category category) {
        Element categoryElement = moduleXml.createElement("category");
        categoryElement.setAttribute("name", category.getName());
        String colorString = Integer.toHexString(category.getColor().getRGB());
        colorString = "#" + colorString.substring(2, colorString.length());
        categoryElement.setAttribute("color", colorString);
        
        for (Tool tool : category.getTools()) {
            Element toolElement = moduleXml.createElement("tool");
            toolElement.setAttribute("name", tool.getName());
            toolElement.setTextContent(tool.getDescription());
            System.out.println(tool.getDescription());
            categoryElement.appendChild(toolElement);
        }
        
        getFirstModule().appendChild(categoryElement);
    }
    
    /**
     * @return all categories contained in the inner XML.
     */
    public List<Category> getCategories() {
        NodeList categoryList = getFirstModule().getElementsByTagName("category");
        for (int i=0; i<categoryList.getLength(); i++) {
            Element categoryElement = (Element) categoryList.item(i);
            Category category = new Category(categoryElement.getAttribute("name"),
                                             categoryElement.getAttribute("color"));
            NodeList toolList = categoryElement.getElementsByTagName("tool");
            for (int j=0; j<toolList.getLength(); j++) {
                Element toolElement = (Element) toolList.item(j);
                category.addTool(toolElement.getAttribute("name"),
                                 toolElement.getTextContent());
            }
            categories.add(category);
        }
        return categories;
    }
    
    @Override
    public void unmarshal(MapMessage from) throws JMSException {
        super.unmarshal(from);
        this.setModuleName(from.getStringProperty(KEY_MODULE_NAME));
        this.moduleXml = XmlUtil.stringToXML(from.getString(KEY_MODULE));
    }
    
    @Override
    public void marshal(MapMessage to) throws JMSException {
        super.marshal(to);
        to.setStringProperty(KEY_MODULE_NAME, this.getModuleName());
        to.setString(KEY_MODULE, XmlUtil.xmlToString(moduleXml));
    }
    
    /**
     * Tool category. Contains several related tools.
     */
    public static class Category {
        private String name;
        private List<Tool> tools = new LinkedList<Tool>();
        private Color color;
        
        /**
         * @param name - name for this category.
         * @param color - String representing hexidecimal RGB value (e.g. "FF1122")
         */
        public Category(String name, String color) {
            this.name = name;
            this.color = Color.decode(color);
        }
        
        public String getName() {
            return name;
        }
        
        public Color getColor() {
            return color;
        }
        
        public void addTool(String name, String description) {
            tools.add(new Tool(name, description));
        }
        
        public List<Tool> getTools() {
            return tools;
        }
    }
    
    /**
     * Single analysis tool.
     */
    public static class Tool {
        private String name;
        private String description;
        
        public Tool(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
