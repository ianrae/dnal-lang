package com.github.ianrae.dnalparse.parser;

import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;

import com.github.ianrae.dnalparse.parser.ast.IdentExp;
import com.github.ianrae.dnalparse.parser.ast.ImportExp;
import com.github.ianrae.dnalparse.parser.ast.PackageExp;

public class PackageParser extends ParserBase {

    public static Parser<PackageExp> packageDecl() {
        return Parsers.sequence(term("package"), VarParser.ident().many().sepBy(term(".")).
                map(new org.codehaus.jparsec.functors.Map<List<List<IdentExp>>, PackageExp>() {
                    @Override
                    public PackageExp map(List<List<IdentExp>> arg0) {
                        StringBuilder sb = new StringBuilder();
                        boolean flag = false;
                        if (! arg0.isEmpty()) {
                            for(List<IdentExp> sublist : arg0) {
                                if (! sublist.isEmpty()) {
                                    if (flag) {
                                        sb.append('.');
                                    }
                                    sb.append(sublist.get(0).val);
                                    flag = true;
                                }
                            }
                        }
                        return new PackageExp(sb.toString());
                    }
                }));
    }    
    
    public static Parser<ImportExp> importDecl() {
        return Parsers.sequence(term("import"), VarParser.ident().many().sepBy(term(".")).
                map(new org.codehaus.jparsec.functors.Map<List<List<IdentExp>>, ImportExp>() {
                    @Override
                    public ImportExp map(List<List<IdentExp>> arg0) {
                        StringBuilder sb = new StringBuilder();
                        boolean flag = false;
                        if (! arg0.isEmpty()) {
                            for(List<IdentExp> sublist : arg0) {
                                if (! sublist.isEmpty()) {
                                    if (flag) {
                                        sb.append('.');
                                    }
                                    sb.append(sublist.get(0).val);
                                    flag = true;
                                }
                            }
                        }
                        return new ImportExp(sb.toString());
                    }
                }));
    }    
     
}