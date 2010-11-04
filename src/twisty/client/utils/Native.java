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

import java.util.ArrayList;

/**
 * Simple API for interacting with native objects.
 */
public class Native {
    
    public static native void set(Object target, String property, Object value) /*-{
        try {
            target[property] = value;
        }
        catch(e) {}
    }-*/;
    
    public static native void set(Object target, String property, int value) /*-{
        try {
            target[property] = value;
        }
        catch(e) {}
    }-*/;
    
    public static native void set(Object target, String property, String value) /*-{
        try {
            target[property] = value;
        }
        catch(e) {}
    }-*/;
    
    public static native String _string(Object target, String property) /*-{
        var rtn = "";
        try {
            if (target[property] === null) 
               rtn = "null";
            else if (target[property] === undefined) 
               rtn = "undefined";
            else
               rtn = target[property].toString();
        }
        catch(e) {}
        return(rtn);
    }-*/;
    
    public static native int _int(Object target, String property) /*-{
        var rtn = 0;
        try {
            rtn = Number(target[property]);
            if (rtn == NaN)
                rtn = 0;
        }
        catch(e) {}
        return(rtn);
    }-*/;
    
    public static native boolean _boolean(Object target, String property) /*-{
        var rtn = false
        try {
            rtn = Boolean(target[property]);
        }
        catch(e) {}
        return(rtn);
    }-*/;
    
    public static native Object _hash(Object target, String property) /*-{
        var rtn = {};
        try {
            rtn = target[property];
        }
        catch(e) {}
        return(rtn);
    }-*/;
    
    public static native Object _list(Object target, String property) /*-{
        var rtn = [];
        try {
            rtn = target[property].toString();
        }
        catch(e) {}
        return(rtn);
    }-*/;
    
    /** Special method to return the attributes of an object.
     * <p>
     * Returns an ArrayList of string properties. For an array
     * these properties will be '0', '1', '2', etc.
     * <p>
     * For object that have no properties the array list will be
     * empty.
     */
    public static ArrayList <String> _properties(Object target) {
        ArrayList <String> rtn = new ArrayList <String> ();
        _propertiesFetch(target, rtn);
        return(rtn);
    }
    
    /** Callback for _properties() */
    private static void _propertiesSet(ArrayList <String> p, String v) {
        p.add(v);
    }
    
    /** Iterates through and fetches properties. */
    private static native String _propertiesFetch(Object target, ArrayList <String> rtn) /*-{
        try {
             for (var key in target) {
                 @twisty.client.utils.Native::_propertiesSet(Ljava/util/ArrayList;Ljava/lang/String;)(rtn, key.toString());
             }
        }
        catch(e) {
            alert(e.message);
        }
    }-*/;
    
    /** Special method to return the appropriate error details. */
    public static native String _error(Object target) /*-{
        var rtn = "";
        try {
            if (target.message !== undefined) 
                rtn = target.message;
            else
                rtn = target.toString();
        }
        catch(e) {}
        return(rtn);
    }-*/;
    
    /** Special method for throwing an uncaught exception. */
    public static void _throw(String message) {
    	__throw(message);
    }
    
    /** Special method for throwing an uncaught exception. */
    public static native void __throw(String message) /*-{
    	throw(message);
    }-*/;
}
