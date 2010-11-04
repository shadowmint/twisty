/** 
 * Copyright 2010 Douglas Linder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twisty.client.utils;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import java.util.ArrayList;
import java.util.Map;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Node;
import com.google.gwt.dom.client.Element;

import twisty.client.utils.Xml;

/** Helper class for creating common CSS components. */
public class CssFactory {
    
    /** 
     * Creates an HTML block from an XML definition file. 
     * <p>
     * The root element of the XML file must be a node of type 'root',
     * and any dynamically inserted elements must be nodes of type 'asset',
     * passed in via the assets array.
     * <p>
     * There can only be one child element for the root; if more exist
     * they are ignored.
     */
    public static Element createCssBlock(Xml template, Map<String, String> styles, Map<String, Element> assets) throws Exception {
    	Element rtn = null;
    	
    	if (template == null)
    		throw new Exception("Invalid template (null)");
    	else if (styles == null)
    		throw new Exception("Invalid style list (null)");
    	
    	Node root = template.node("root");
        
    	// This is the first actual element to process.
    	NodeList root_children = root.getChildNodes();
    	root = null;
    	for (int i = 0; i < root_children.getLength(); ++i) {
    		Node item = root_children.item(i);
    		String type = item.getNodeName();
    		if (!type.equals("#text")) {
    			root = item;
    			break;
    		}
    	}
    	if (root == null) {
    		Exception e = new Exception("Invalid XML data. Bad root node.");
    		throw(e);
    	}
    	
    	// Init first child...
    	ArrayList<Child> children = new ArrayList<Child>();
		rtn = createCssBlockNode(root, styles, assets);
		createCssBlockProcessChildren(root, children, rtn);
        
    	// Process all children
		while(children.size() > 0) {
    		Child folder = children.remove(0);
    		Element parent = folder.parent;
    		Element child_element = createCssBlockNode(folder.child, styles, assets);
    		if (child_element != null) {
	    		parent.appendChild(child_element);
				createCssBlockProcessChildren(folder.child, children, child_element);
    		}
		}
    	
    	return(rtn);
    }
    
	/** 
     * Handler for processing a single child node. 
     * <p>
     * The only supported attributes for nodes are class and id.
     * <p>
     * Ie. Don't try to use images here. 
     */
    private static Element createCssBlockNode(Node root, Map<String, String> styles, Map<String, Element> assets) {
    	
    	Element rtn = null;
    	boolean text_node = false;
    	boolean is_asset = false;
    	
    	// Is this an asset?
    	// Assets are hard coded and have no child objects.
    	if(root.getNodeName().equals("asset")) {
    		Node id = root.getAttributes().getNamedItem("id");
    		if (id != null) {
	    		String target = id.getNodeValue();
	    		rtn = null;
	    		if (assets != null) {
		    		rtn = (Element) assets.get(target);
		    		is_asset = true;
	    		}
    		}
    	}
    	
    	// Nope, treat it as a normal node. 
    	else {
    		String type = root.getNodeName();
    		if(type.equals("#text")) {
    			rtn = Document.get().createSpanElement();
    			rtn.setInnerHTML(root.getNodeValue());
    			text_node = true;
    		}
    		else {
    			Document d = Document.get();
	    		rtn = d.createElement(type);
    		}
    	}
    	
    	// Text nodes don't get styles, etc. applied.
    	if ((!text_node) && (rtn != null)) {
    		
	    	// Add class 
			NamedNodeMap attrs = root.getAttributes();
			if (attrs != null) {
				int size = attrs.getLength();
				for (int i = 0; i < size; ++i) {
					Node attr = attrs.item(i);
					if (attr != null) {
						String key = attr.getNodeName();
						String value = attr.getNodeValue();
						if (!value.trim().equals("")) {
							
							// Style elements are special; we can't simply set the style property.
							if (key.equals("style")) {
								try {
									Style st = rtn.getStyle();
									String[] sts = attr.getNodeValue().split(";");
									for (String style : sts) {
										String props[] = style.split(":");
										if (props.length > 1)
											st.setProperty(props[0].trim(), props[1].trim());
									}
								}
								catch(Exception error) {
								}
							}
							
							// Class values must be looked up in the hash.
							else if (key.equals("class")) {
								String final_class = rtn.getClassName(); // Always prefix with existing.
								String[] template_classes = value.trim().split(" ");
								for (String template_class : template_classes) {
									if (template_class.length() > 0) {
										String fudged = styles.get(template_class);
										if (fudged == null) 
											final_class += " " + template_class; // Pass through unremarked classes.
										else 
											final_class += " " + fudged; // Pass through altered name.
									}
								}
								rtn.setClassName(final_class);
							}
							
							// Asset id's won't be universally unique; can't set them here.
							else if (key.equals("id") && is_asset) {
							}
							
							// Everything else is fine!
							else
								rtn.setAttribute(key, value.trim());
						}
					}
				}
			}
    	}
    	
    	return(rtn);
    }
    
    /** 
     * Processes child nodes for createCssBlock.
     */
    private static void createCssBlockProcessChildren(Node root, ArrayList<Child> children, Element parent) {
		if (root.hasChildNodes()) {
			Child folder = null;
			for (int i = 0; i < root.getChildNodes().getLength(); ++i) {
				String type = root.getChildNodes().item(i).getNodeName();
				String value = root.getChildNodes().item(i).getNodeValue();
				if ((type.equals("#text") && (!value.trim().equals(""))) || 
					(!type.equals("#text"))) {
					folder = new Child();
					folder.child = root.getChildNodes().item(i);
					folder.parent = parent;
					children.add(folder);
				}
			}
    	}
    }
    
    /** 
     * Creates a span with a string content.
     * <p>
     * Many templates just require text substitution for asset
     * place holders; this is a shortcut function to create
     * spans for that purpose.
     */
    public static Element createCssNode(String content) {
    	Element rtn = Document.get().createSpanElement();
    	rtn.setInnerHTML(content);
    	return(rtn);
    }
    
    /** 
     * Creates a span with a string content that links to a url.
     * <p>
     * Many templates just require text substitution for asset
     * place holders; this is a shortcut function to create
     * spans that link to content.
     * <p>
     * Both the link target and url are required. Typically
     * _blank is an appropriate target.
     */
    public static AnchorElement createCssNode(String content, String url, String target) {
    	AnchorElement rtn = Document.get().createAnchorElement();
    	Element c = Document.get().createSpanElement();
    	c.setInnerHTML(content);
    	rtn.appendChild(c);
    	rtn.setHref(url);
    	rtn.setTarget(target);
    	return(rtn);
    }
    
    /**
     * Used for keeping track of children and their parents.  
     */
    private static class Child {
    	public Node child = null;
    	public Element parent = null;
    }
}
