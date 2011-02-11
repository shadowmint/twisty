package twisty.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import twisted.client.ComponentLog;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.ListStyleType;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

public class SortableTable extends Widget {

	/** Column type enum */
	public enum Type {
		HTML, WIDGET, TEXT, ELEMENT
	};
	
	/** Current sorter object: ascending */
	private Comparator<Sortable> upSorter = null;

	/** Current sorter object: descending */
	private Comparator<Sortable> downSorter = null;

	/** Number of rows. */
	private int rows = 0;

	/** Column list. */
	private ArrayList<Column> columns = new ArrayList<Column>();
	
	/** Named columns */
	private HashMap<String, Column> namedColumns = new HashMap<String, Column>();
	
	/** Style keys. */
	private HashMap<String, String> styleKeys = new HashMap<String,String>();
	
	/** Header. */
	private Element headerElement = null;
	
	/** Rows. */
	private Element rowsElement = null;
	
	/** Cached UA string. */
	private String ua = null;
	
	/** If this is IE; yes, we should be using deferred binding... */
	private boolean isIE = false;
	
	/** Header container class style key. */
	private static final String STYLE_INLINE_BLOCK = "SortableTable-Inline-Block";
	
	/** Header container class style key. */
	public static final String STYLE_HEADER_CONTAINER = "SortableTable-HeaderContainer";
	
	/** Header cell class style key. */
	public static final String STYLE_HEADER_CELL = "SortableTable-HeaderCell";
	
	/** Header cell sorted up class style key. */
	public static final String STYLE_HEADER_SORT_UP = "SortableTable-HeaderSortedUp";
	
	/** Header cell sorted down class style key. */
	public static final String STYLE_HEADER_SORT_DOWN = "SortableTable-HeaderSortedDown";
	
	/** Header named cell class prefix style key. */
	public static final String STYLE_NAME_PREFIX = "SortableTable-Field-";
	
	/** Rows container class style key. */
	public static final String STYLE_ROWS_CONTAINER = "SortableTable-RowsContainer";
	
	/** Row container class style key. */
	public static final String STYLE_ROW_CONTAINER = "SortableTable-RowContainer";
	
	/** Row cell class style key. */
	public static final String STYLE_ROW_CELL = "SortableTable-RowCell";
	
	public SortableTable() {
		styleKeys.put(STYLE_HEADER_CONTAINER, STYLE_HEADER_CONTAINER);
		styleKeys.put(STYLE_HEADER_CELL, STYLE_HEADER_CELL);
		styleKeys.put(STYLE_ROWS_CONTAINER, STYLE_ROWS_CONTAINER);
		styleKeys.put(STYLE_ROW_CONTAINER, STYLE_ROW_CONTAINER);
		styleKeys.put(STYLE_NAME_PREFIX, STYLE_NAME_PREFIX);
		styleKeys.put(STYLE_ROW_CELL, STYLE_ROW_CELL);
		styleKeys.put(STYLE_HEADER_SORT_UP, STYLE_HEADER_SORT_UP);
		styleKeys.put(STYLE_HEADER_SORT_DOWN, STYLE_HEADER_SORT_DOWN);
		
		// Create widget. 
		rowsElement = createRowsContainer();
		headerElement = createHeaderContainer();
		Element root = Document.get().createDivElement();
		root.appendChild(headerElement);
		root.appendChild(rowsElement);
		setElement(root);
		
		// Default sorters
		setSorter(true, new Comparator<Sortable>() {
			public int compare(Sortable o1, Sortable o2) {
				int result = o1.e.getInnerHTML().compareTo(o2.e.getInnerHTML());
				return(result);
			}
		});
		setSorter(false, new Comparator<Sortable>() {
			public int compare(Sortable o1, Sortable o2) {
				int result = o1.e.getInnerHTML().compareTo(o2.e.getInnerHTML());
				result = -result;
				return(result);
			}
		});
	}
	
	/** Allows style keys to be changes. */
	public void setStyleKey(String key, String style) {
		styleKeys.put(key, style);
	}

	/** Adds a column to the table. */
	public void addColumn(String name, int width) {
		addNamedColumn(name, name, width);
	}

	/** Adds a column to the table; the column is named name, but shown as displayName. */
	public void addNamedColumn(String name, String displayName, int width) {
		
		// Create native data
		Element column = createHeaderCell(name);
		if (width > 0)
			column.getStyle().setWidth(width, Unit.PX);
		
		// Create column object
		Column c = new Column();
		c.width = width;
		c.name = name;
		c.cells = new ArrayList<Element>();
		c.header = column;
		
		// Add a sort event handler to the header
		Anchor a = new Anchor();
		final Column target = c;
		a.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				Column current = sortKeyColumn();
				if ((target != null) && (current != null)) {
					if (current != target) 
						sort(target.name, current.ascending);
					else
						sort(target.name, !(current.ascending));
				}
				else
					sort(target.name, true);
			}
		});
		a.setHTML(name);
		getMagicPanel(column).add(a);
		
		namedColumns.put(name, c);
		columns.add(c);
		
		// Update columns data
		headerElement.appendChild(column);
	}

	/** Set an entire column of objects. */
	public void addRow(HashMap<String, Type> types, HashMap<String, Object> values) {
		
		// Create native row
		Element row = createRowContainer();
		
		// Process columns
		for (Column c : columns) {
			String name = c.name;
			Type type = types.get(name);
			if ((c != null) && (type != null)) {
				Element cell = createRowCell(name);
				c.cells.add(cell);
				if (c.width > 0)
					cell.getStyle().setWidth(c.width, Unit.PX);
				if (type == Type.TEXT) {
					String value = (String) values.get(name);
					cell.setInnerText(value);
				}
				else if (type == Type.HTML) {
					String value = (String) values.get(name);
					cell.setInnerHTML(value);
				}
				else if (type == Type.ELEMENT) {
					Element value = (Element) values.get(name);
					cell.appendChild(value);
				}
				else if (type == Type.WIDGET) {
					MagicPanel p = this.getMagicPanel(cell);
					Widget value = (Widget) values.get(name);
					p.add(value);
				}
				row.appendChild(cell);
			}
		}
		
		// Add row
		rowsElement.appendChild(row);
	}

	/** Clears any held rows. */
	public void clear() {
		rowsElement.setInnerHTML("");
		for (Column c : columns) {
			c.cells.clear();
		}
	}
	
	/** Clears any held rows and columns. */
	public void clearAll() {
		rowsElement.setInnerHTML("");
		headerElement.setInnerHTML("");
	}
	
	/** Sorts by a column. */
	public void sort(String column) {
		sort(column, true);
	}
	
	/** Set the sort comparator. */
	public void setSorter(boolean ascending, Comparator<Sortable> sorter) {
		if(ascending)
			upSorter = sorter;
		else
			downSorter = sorter;
	}
	
	/** Returns the current sort key. */
	private Column sortKeyColumn() {
		Column rtn = null;
		for (Column item : columns) {
			if(item.sortPrimary) {
				rtn = item;
				break;
			}
		}
		return(rtn);
	}

	/** Sorts by a column, specifying custom comparator. */
	public void sort(String column, boolean ascending) {
		Column c = namedColumns.get(column);
		
		// Remove old style
		Column current = sortKeyColumn();
		if (current != null) {
			removeStyle(current.header, styleKeys.get(STYLE_HEADER_SORT_DOWN));
			removeStyle(current.header, styleKeys.get(STYLE_HEADER_SORT_UP));
			current.sortPrimary = false;
		}
		
		// Update styles
		c.ascending = ascending;
		c.sortPrimary = true;
		if (ascending) {
			removeStyle(c.header, styleKeys.get(STYLE_HEADER_SORT_DOWN));
			addStyle(c.header, styleKeys.get(STYLE_HEADER_SORT_UP));
		}
		else {
			removeStyle(c.header, styleKeys.get(STYLE_HEADER_SORT_UP));
			addStyle(c.header, styleKeys.get(STYLE_HEADER_SORT_DOWN));
		}
		
		// Create metadata and sort.
		ArrayList<Sortable> list = new ArrayList<Sortable>();
		int offset = 0;
		for(Element e : c.cells) {
			Sortable s = new Sortable();
			s.e = e;
			s.c = c;
			s.offset = offset;
			list.add(s);
			++offset;
		}
		if (ascending) 
			Collections.sort(list, upSorter);
		else
			Collections.sort(list, downSorter);
		repopulateSortedData(list);
	}
	
	/** Repopulate table from sorted data. */
	private void repopulateSortedData(List<Sortable> list) {
		for (Sortable s : list) {
			Element row = s.e.getParentElement();
			ComponentLog.trace("Affecting row: " + row);
			if (row != null) {
				rowsElement.removeChild(row);
				rowsElement.appendChild(row); // At the bottom now.
			}
		}
	}
	
	/** Creates the header container. */
	private Element createHeaderContainer() {
		Element rtn = Document.get().createDivElement();
		addStyle(rtn, styleKeys.get(STYLE_HEADER_CONTAINER));
		return(rtn);
	}
	
	/** Creates the rows container. */
	private Element createRowsContainer() {
		Element rtn = Document.get().createDivElement();
		addStyle(rtn, styleKeys.get(STYLE_ROWS_CONTAINER));
		return(rtn);
	}
	
	/** Creates a row container. */
	private Element createRowContainer() {
		Element rtn = Document.get().createDivElement();
		addStyle(rtn, styleKeys.get(STYLE_ROW_CONTAINER));
		return(rtn);
	}
	
	/** Creates a header cell. */
	private Element createHeaderCell(String name) {
		Element rtn = createInlineBlock();
		addStyle(rtn, styleKeys.get(STYLE_HEADER_CELL));
		addStyle(rtn, styleKeys.get(STYLE_NAME_PREFIX) + name);
		return(rtn);
	}
	
	/** Creates a row cell. */
	private Element createRowCell(String name) {
		Element rtn = createInlineBlock();
		addStyle(rtn, styleKeys.get(STYLE_ROW_CELL));
		addStyle(rtn, styleKeys.get(STYLE_NAME_PREFIX) + name);
		return(rtn);
	}
	
	/** Creates a magical inline block that works across browsers. */
	private Element createInlineBlock() {
		Element rtn = Document.get().createLIElement();
		try {
			if (ua == null) {
				ua = nav();
				if (ua.toLowerCase().indexOf("msie") != -1) {
					if (ieVersion() < 8) 
						isIE = true;
				}
			}
			rtn.getStyle().setListStyleType(ListStyleType.NONE);
			rtn.getStyle().setOverflow(Overflow.HIDDEN);
			rtn.getStyle().setProperty("zoom", "1");
			if (isIE)
				rtn.getStyle().setProperty("display", "inline");
			else {
				rtn.getStyle().setProperty("display", "inline-block");
				rtn.getStyle().setProperty("display", "-moz-inline-stack");
			}
		}
		catch(Exception e) {
		}
		rtn.setClassName(STYLE_INLINE_BLOCK);
		return(rtn);
	}
	
	/** Native browser detect hack. */
	private native String nav() /*-{
		return(navigator.userAgent);
	}-*/;
	
	/** IE version detect hack; yeah, sad. */
	private native float ieVersion() /*-{
	    var rv = -1; // Return value assumes failure.
        var ua = navigator.userAgent;
        var re  = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
        if (re.exec(ua) != null)
	        rv = parseFloat( RegExp.$1 );
	    return rv; // Force int.
	}-*/;
	
	/** Adds an extra style class to an object. */
	private void addStyle(Element e, String classname) {
		e.setClassName(e.getClassName() + " " + classname);
	}
	
	/** Removes a specific style class from an object. */
	private void removeStyle(Element e, String classname) {
		String original = e.getClassName();
		String styles[] = original.split(" ");
		String new_style = null;
		for (String i : styles) {
			if (!i.equals(classname)) {
				if (new_style != null)
					new_style += " ";
				else
					new_style = "";
				new_style += i;
			}
		}
		e.setClassName(new_style);
	}
	
	/** Represents a single column. */
	private class Column {
		
		/** Pixels wide; or -ve for not specified. */
		int width;
		
		/** Name of this column. */
		String name;
		
		/** Set of elements (ordered) in this column. */
		ArrayList<Element> cells;
		
		/** If this column is the master sort key. */
		boolean sortPrimary; 
		
		/** If this column is asc or desc sorted. */
		boolean ascending;
		
		/** The header cell. */
		Element header;
	}
	
	/** Panel cache. */
	private static HashMap<Element, MagicPanel> magicPanelInstances = new HashMap<Element,MagicPanel>();
	
	/** Returns a magic panel. */
	private MagicPanel getMagicPanel(Element e) {
		MagicPanel rtn = magicPanelInstances.get(e);
		if (rtn == null) {
			rtn = new MagicPanel(e);
			magicPanelInstances.put(e, rtn);
		}
		return(rtn);
	}
		
	/** Makes an element a valid GWT container. */
	private class MagicPanel extends AbsolutePanel {
		public MagicPanel(Element root) {
			super(root.<com.google.gwt.user.client.Element> cast());
            onAttach();
		}
	}
	
	/** Sortable container. */
	public class Sortable {
		public Column c;
		public Element e;
		int offset;
	}
}
