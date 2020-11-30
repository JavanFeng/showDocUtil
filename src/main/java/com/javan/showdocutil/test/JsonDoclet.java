package com.javan.showdocutil.test;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.internal.toolkit.AbstractDoclet;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;

public class JsonDoclet extends AbstractDoclet {

    @Override
    public Configuration configuration() {
        return new ConfigurationImpl();
    }

    @Override
    protected void generateProfileFiles() throws Exception {

    }

    @Override
    protected void generatePackageFiles(ClassTree classTree) throws Exception {

    }

    @Override
    protected void generateClassFiles(ClassDoc[] classDocs, ClassTree classTree) {

    }
}