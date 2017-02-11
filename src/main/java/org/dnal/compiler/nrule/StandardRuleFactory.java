package org.dnal.compiler.nrule;

import org.dnal.compiler.dnalgenerate.CustomRuleFactory;
import org.dnal.compiler.dnalgenerate.RuleDeclaration;
import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.virtual.VirtualDataItem;
import org.dnal.core.nrule.virtual.VirtualDate;
import org.dnal.core.nrule.virtual.VirtualInt;
import org.dnal.core.nrule.virtual.VirtualList;
import org.dnal.core.nrule.virtual.VirtualLong;
import org.dnal.core.nrule.virtual.VirtualPseudoLen;
import org.dnal.core.nrule.virtual.VirtualString;

public class StandardRuleFactory  {

    public static class Factory implements RuleFactory {

        private RuleDeclaration decl;

        public Factory(String ruleName, Shape shape) {
            this.decl = new RuleDeclaration(ruleName, shape);
        }
        public Factory(String ruleName, Shape shape1, Shape shape2) {
            this.decl = new RuleDeclaration(ruleName, shape1);
            this.decl.shapeL.add(shape2);
        }
        public Factory(String ruleName, Shape shape1, Shape shape2, Shape shape3) {
            this.decl = new RuleDeclaration(ruleName, shape1);
            this.decl.shapeL.add(shape2);
            this.decl.shapeL.add(shape3);
        }
        public Factory(String ruleName, Shape shape1, Shape shape2, Shape shape3, Shape shape4) {
            this.decl = new RuleDeclaration(ruleName, shape1);
            this.decl.shapeL.add(shape2);
            this.decl.shapeL.add(shape3);
            this.decl.shapeL.add(shape4);
        }

        //support long later!!
        @Override
        public NRule createRule(String ruleName, Shape shape) {
            NRule rule = null;
            switch(ruleName) {
            case "empty":
                rule = new EmptyRule(ruleName, (VirtualString) createForShape(shape));
                break;
            case "regex":
                rule = new RegexRule(ruleName, (VirtualString) createForShape(shape));
                break;
            case "range":
                if (shape.equals(Shape.DATE)) {
                    rule = new DateRangeRule(ruleName, (VirtualDate) createForShape(shape));
                } else if (shape.equals(Shape.INTEGER)) {
                    rule = new IntegerRangeRule(ruleName, (VirtualInt) createForShape(shape));
                } else if (shape.equals(Shape.LONG)){
                    rule = new LongRangeRule(ruleName, (VirtualLong) createForShape(shape));
                } else if (shape.equals(Shape.STRING)) {
                    rule = new StringRangeRule(ruleName, (VirtualString)createForShape(shape), true);
                }
                break;
            case "rangei":
                if (shape.equals(Shape.STRING)) {
                    rule = new StringRangeRule(ruleName, (VirtualString)createForShape(shape), false);
                }
                break;
            case "len":
                rule = new LenRule(ruleName, new VirtualPseudoLen());
                break;
            case "in":
                rule = new InRule(ruleName, (VirtualInt) createForShape(shape));
                break;
            case "startsWith":
                rule = new StartsWithRule(ruleName, (VirtualString) createForShape(shape));
                break;
            case "endsWith":
                rule = new EndsWithRule(ruleName, (VirtualString) createForShape(shape));
                break;
            default:
                break;        
            }
            return rule;
        }
        

        private VirtualDataItem createForShape(Shape shape) {
            switch(shape) {
            case STRING:
                return new VirtualString();
            case DATE:
                return new VirtualDate();
            case INTEGER:
                return new VirtualInt();
            case LONG:
                return new VirtualLong();
            case LIST:
                return new VirtualList();
            case STRUCT:
                return new VirtualString(); //!!fix later
            default:
                return null;
            }
        }
        @Override
        public RuleDeclaration getDeclaration() {
            return decl;
        }
    }    
    public CustomRuleFactory createFactory() {
        CustomRuleFactory crf = new CustomRuleFactory();
        crf.addFactory(new Factory("empty", Shape.LIST, Shape.STRING));
        crf.addFactory(new Factory("regex", Shape.STRING));
        crf.addFactory(new Factory("startsWith", Shape.STRING));
        crf.addFactory(new Factory("endsWith", Shape.STRING));
        crf.addFactory(new Factory("range", Shape.DATE, Shape.INTEGER, Shape.LONG, Shape.STRING));
        crf.addFactory(new Factory("rangei", Shape.DATE, Shape.STRING));
        crf.addFactory(new Factory("len", Shape.LIST, Shape.STRING));
        crf.addFactory(new Factory("in", Shape.INTEGER));
        return crf;
    }

}
