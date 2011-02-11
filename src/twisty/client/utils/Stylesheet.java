package twisty.client.utils;


/**
 * Simple API for interacting with native style objects.
 */
public class Stylesheet {
    
    /** Native instance. */
    private Object _native = null;
    
    /** Create a stylesheet interface with native reference. */
    public Stylesheet(Object object) {
        _native = object;
    }
    
    /** 
     * Loads a style sheet by title. 
     * <p>
     * The style sheet in the document needs a title for this to work.
     * <p>
     * Use something like: &lt; style title="thisTarget" &gt; ... 
     * <p>
     * You can't use this to create dynamically created stylesheets. It just doesn't work.
     * @param title The title of the stylesheet to fetch.
     */
    public static Stylesheet loadStylesheet(String title) {
        Stylesheet rtn = null;
        Object _native = _loadStylesheet(title);
        if (_native != null)
            rtn = new Stylesheet(_native);
        return(rtn);
    }
    
    /** 
     * Creats a style sheet by title.
     * <p>
     * If a stylesheet can be loaded using loadStylesheet, this
     * call does not create a new one, it returns the existing
     * instance instead.
     * <p>
     * @param title The title of the stylesheet to create.
     */
    public static Stylesheet createStylesheet(String css) throws Exception {
        Stylesheet rtn = null;
    	Object n = _createStylesheet(css);
    	if (n != null) 
            rtn = new Stylesheet(n);
        return(rtn);
    }
    
    private static native Object _loadStylesheet(String title) /*-{
        var rtn = null;
        try {
            var sheet, i;
            var set = $doc.getElementsByTagName("style");
            for(i = 0; i < set.length; i++) {
                sheet = set[i];
                if (sheet.title == title) {
                    rtn = sheet;
                    if (rtn.styleSheet)
                        rtn = rtn.styleSheet;
                    else if (rtn.sheet)
                        rtn = rtn.sheet;
                    break;
                } 
            }
            alert("Found: " + rtn);
        }
        catch(error) {}
        return(rtn);
    }-*/;
    
    private static native Object _createStylesheet(String css) /*-{
    	var rtn = null;
        try {
        	rtn = $doc.createElement('style');
        	rtn.type = "text/css";
        	rtn.rel = "stylesheet";
        	rtn.media = "screen";
        	try {
        		rtn.styleSheet.cssText = css; // ie code
        	}
        	catch(errors) {
        	}	
        	try {
        		rtn.appendChild($doc.createTextNode(css)); // FAll back for others
        	}
        	catch(errors) {
        	}	
		    $doc.getElementsByTagName('head')[0].appendChild(rtn);
		    rtn = $doc.styleSheets[$doc.styleSheets.length - 1];
        }
        catch(error) {
        }
        return(rtn);
    }-*/;
    
    /** 
     * Sets a CSS rule. 
     * <p>
     * If any rules already exist for the given selector they are replaced
     * by the new rule.
     * <p>
     * As such, the rule should be a composite; eg. "border: 1px solid #000; font-size: 15px;"
     * <p>
     * The selector may be any normal css selector; eg. "#my .class a".
     * <p>
     * Note that this affects ONLY the current style sheet. To force a style update on a specific
     * element apply the action to that elements style property directly.
     * @param selector The CSS selector rule.
     * @param rule The CSS rule to apply to the given selector.
     */
    public void set(String selector, String rule) {
        int index = _getRuleIndex(_native, selector);
        if (index != -1)
            _deleteRule(_native, index);
        _addRule(_native, selector, rule);
    }
    
    /** Returns true if a rule exists for the given selector. */
    public boolean has(String selector) {
    	boolean rtn = false;
	    int index = _getRuleIndex(_native, selector);
	    if (index != -1)
	    	rtn = true;
	    return(rtn);
    }
    
    private static native void _addRule(Object sheet, String selector, String rule) /*-{
        try {
            if (sheet.addRule) 
                sheet.addRule(selector, rule);
            else if (sheet.insertRule)
                sheet.insertRule(selector + "{ " + rule + " }", 0);
        }
        catch(e) {
        }
    }-*/;
    
    private static native int _getRuleIndex(Object sheet, String selector) /*-{
        var rtn = -1;
        try {
            var set = null;
            if (sheet.cssRules)
                set = sheet.cssRules;
            else 
                set = sheet.rules;
            if (set) {
                var i;
                for (i = 0; i < set.length; ++i) {
                    if (set[i].selectorText == selector) {
                        rtn = i;
                        break;
                    }
                }
            }
        }
        catch(e) {}
        return(rtn);
    }-*/;
    
    private static native void _deleteRule(Object sheet, int index) /*-{
        try {
            if (sheet.deleteRule) 
                sheet.deleteRule(index);
            else if (sheet.removeRule)
                sheet.removeRule(index);
        }
        catch(e) {}
    }-*/;
}
