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

import java.util.HashMap;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/** 
 * Useful API to accessing XML data.
 * <p>
 * Add <inherits name='com.google.gwt.xml.XML'/> to your config XML to use this.
 * <p>
 * You may also have to import com.google.gwt.xml.client.Node.
 */
public class Xml {
	
	/** XML Data */
	Document _doc = null;
	
	/** 
	 * Tries to parse a string as an XML block.
	 * <p>
	 * This is destroys any held XML data. 
	 * @return True on success and false on failure.
	 */
	public boolean parse(String xml) {
		boolean rtn = false;
		try {
			_doc = XMLParser.parse(xml);
			if (_doc != null)
    			rtn = true;
		}
		catch(Exception e) {
		}
		return(rtn);
	}
	
	/** Creates a new internal XML document. */
	public void create() {
		_doc = XMLParser.createDocument();
	}
	
	/** Adds a single node to the XML tree. */
	public void append(String name, String value) {
		append(_doc, name, value, null);
	}
	
	/** Adds a single node to the XML tree. */
	public void append(String name, String value, HashMap<String,String> properties) {
		append(_doc, name, value, properties);
	}

	/** Adds a single node to the XML tree. */
	public void append(Node parent, String name, String value) {
		append(parent, name, value, null);
	}

	/** Adds a single node to the XML tree. */
	public void append(Node parent, String name, String value, HashMap<String,String> properties) {
		append(parent, name, value, properties, false);
	}
	
	/** Adds a single node to the XML tree; forces CDATA for the value. */
	public void append(Node parent, String name, String value, HashMap<String,String> properties, boolean useCData) {
		Element child = _doc.createElement(name);
		if (value != null) {
			if (!useCData) {
				Text content = _doc.createTextNode(value);
				child.appendChild(content);
			}
			else {
				Text content = _doc.createCDATASection(value);
				child.appendChild(content);
			}
		}
		if (properties != null) {
			for (String key : properties.keySet()) {
				child.setAttribute(key, properties.get(key));
			}
		}
		parent.appendChild(child);
	}
	
	/** Exports the document. */
	public String export() {
		if (_doc != null)
			return(_doc.toString());
		else
			return("");
	}

	/** Convience function to access the parsed document. */
	public String get(String path) {
		return(get(_doc, path));
	}
	
	/** Convience function to access the parsed document. */
	public int count(String path) {
		return(count(_doc, path));
	}
	
	/** Convience function to access the parsed document. */
	public Node node(String path) {
		return(node(_doc, path));
	}
	
	/** Fetches a specific attribute on a node. */
	public String attr(Node n, String attribute) {
		Node attr = n.getAttributes().getNamedItem(attribute);
		String rtn = attr.getNodeValue();
		return(rtn);
	}
	
	/** Returns an count for an XML node. */
	public int count(Node root, String path) {
		int rtn = 0;
		rtn = findPeerCount(root, path);
		return(rtn);
	}
	
	/** 
	 * Returns an XML node value.
	 * <p>
	 * Push in a path like this:<br/>
	 * document.root.item[index].item
	 * <p>
	 * If no index is specified, it is assumed to be 0.
	 */
	public String get(Node root, String path) {
		String rtn = null;
		Node target = node(root, path);
		if (target != null) 
			rtn = get(target);
		return(rtn);
	}
	
	/** 
	 * Returns an XML node value.
	 * <p>
	 * This function always returns a string. For xml elements
	 * this is the text value of that node. 
	 */
	public String get(Node root) {
		String rtn = root.getNodeValue();
			
		/* See if there are any child text nodes. */
		if ((rtn == null) || (rtn.trim().equals(""))) {
			if (rtn == null) 
				rtn = "";
			NodeList set = root.getChildNodes();
			int count = set.getLength();
			for (int i = 0; i < count; ++i) {
				Node child = set.item(i);
				int type = child.getNodeType();
				if (type == Node.TEXT_NODE) {
					rtn += child.toString();
				}
			}
		}
		
		return(rtn);
	}
	
	/** Builds an XML path. */
	private XmlNode[] buildPath(String path) {
		XmlNode[] rtn = null;
		String[] list = path.split("\\.");
		rtn = new XmlNode[list.length];
		int offset = 0;
		for (String item : list) {
			rtn[offset] = new XmlNode();
			rtn[offset].index = 0;
			if (item.endsWith("]")) {
				try {
					String index = item.substring(item.indexOf("["));
					String value = item.substring(0, item.indexOf("["));
					index = index.replaceAll("[\\[\\]]", "");
					rtn[offset].index = Integer.parseInt(index);
					rtn[offset].name = value; 
				}
				catch(Exception e) {
					rtn[offset].index = 0;
				}
			}
			else
				rtn[offset].name = item;
			++offset;
		}
		return(rtn);
	}
	
	/** Finds a leaf from a path. */
	public Node node(Node root, String path) {
		
		// Safty first
		if (root == null)
			return(null);
		
		XmlNode[] ePath = buildPath(path);
		
		Node self = root;
		NodeList children = self.getChildNodes();
		
		for (int offset = 0; offset < ePath.length; ++offset) {
			
			/** Hunt for a match for the current ePath entry. */
			boolean found = false;
			int count = children.getLength();
			int current_offset = 0;
			for (int child_offset = 0; child_offset < count; ++child_offset) {
				Node child = children.item(child_offset);
				
				/** Current type? */
				if (child.getNodeName().equals(ePath[offset].name)) {
					if (current_offset == ePath[offset].index) {
						self = child;
						children = self.getChildNodes();
						found = true;
						ePath[offset].found = true;
						break;
					}
					++current_offset;
				}
			}
			
			if (!found)
				break;
		}
		
		if (!ePath[ePath.length - 1].found) 
			self = null;

		return(self);
	}
	
	/** Finds a leaf from a path. */
	private int findPeerCount(Node root, String path) {
		int rtn = 0;
		Node self = node(root, path);
		try {
    		String name = self.getNodeName();
    		Node parent = self.getParentNode();
			NodeList children = parent.getChildNodes();
			for (int child_offset = 0; child_offset < children.getLength(); ++child_offset) {
				Node child = children.item(child_offset);
				if (child.getNodeName().equals(name))
					++rtn;
			}
		}
		catch(Exception e) {
			rtn = 0;
		}
		return(rtn);
	}
	
	/** For walking the XML tree. */
	private class XmlNode {
		public String name;
		public int index;
		public boolean found = false;
	}
}
