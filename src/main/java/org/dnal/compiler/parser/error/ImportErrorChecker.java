package org.dnal.compiler.parser.error;

import org.dnal.compiler.et.XErrorTracker;
import org.dnal.compiler.parser.DNALDocument;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.parser.ast.ImportExp;
import org.dnal.compiler.parser.ast.PackageExp;
import org.dnal.core.util.NameUtils;

public class ImportErrorChecker extends ErrorCheckerBase {

    public ImportErrorChecker(DNALDocument doc, XErrorTracker et) {
        super(doc, et);
    }

    public void checkForErrors() {
        boolean haveSeenOther = false;
        boolean haveSeenImport = false;
        int pkgCount = 0;

        for(Exp exp: doc.getStatementList()) {
            if (exp instanceof PackageExp) {
                pkgCount++;
                if (pkgCount > 1) {
                    addError2s("not allowed to have more than one package statement.", "", "");
                } else if (haveSeenOther || haveSeenImport) {
                    addError2s("package must be first statement.", "", "");
                }

                PackageExp pkgExp = (PackageExp) exp;
                if (NameUtils.isNullOrEmpty(pkgExp.val)) {
                    addError2s("package statement is empty.", "", "");
                }
            } else if (exp instanceof ImportExp) {
                if (haveSeenOther) {
                    addError2s("import must be before type or value statements.", "", "");
                }
                haveSeenImport = true;

                ImportExp impExp = (ImportExp) exp;
                if (NameUtils.isNullOrEmpty(impExp.val)) {
                    addError2s("import statement is empty.", "", "");
                } 
            } else {
                haveSeenOther = true;
            }
        }
    }
}