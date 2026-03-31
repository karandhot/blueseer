/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.blueseer.utl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author TerryVa
 */
public class TreeConverter {

    public static MyNodePOJO convertToPOJO(DefaultMutableTreeNode treeNode) {
        if (treeNode == null) {
            return null;
        }

        MyNodePOJO pojoNode = new MyNodePOJO();
        // Get the user object associated with the tree node
        pojoNode.setUserObject(treeNode.getUserObject());

        // Recursively convert the children
        if (treeNode.getChildCount() > 0) {
            List<MyNodePOJO> childrenList = new ArrayList<>();
            // The children() method returns an enumeration
           
            Enumeration<TreeNode> children = treeNode.children(); 
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
                childrenList.add(convertToPOJO(childNode)); // Recursive call
            }
            pojoNode.setChildren(childrenList);
        } else {
            // Set an empty list or null if it's a leaf node
            pojoNode.setChildren(Collections.emptyList());
        }

        return pojoNode;
    }
    
    public static class MyNodePOJO {
    private Object userObject;
    private List<MyNodePOJO> children;
    // You might also want an allowsChildren field if needed

    // Getters and Setters (or use records/lombok)
    public Object getUserObject() {
        return userObject;
    }

    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    public List<MyNodePOJO> getChildren() {
        return children;
    }

    public void setChildren(List<MyNodePOJO> children) {
        this.children = children;
    }
}
   
}