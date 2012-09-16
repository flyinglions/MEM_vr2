package org.flying.lions;

import java.io.IOException;


public class RuleBuilder {

    private RuleReader ruleReaders = new RuleReader();
    private Rule[] ruleList = null;
    private String[] valueArray = null;
    private int ruleLeng = 0;

    RuleBuilder(String ruleNameFile) {
        ruleReaders = new RuleReader(ruleNameFile);
    }

    RuleBuilder() {
        
    }

    public int getRuleLeng() {
        return ruleLeng;
    }

    public Rule[] getRuleList() {
        return ruleList;
    }

    public String[] getValueArray() {
        return valueArray;
    }

    /**
     * Rule list:
     * 1 - Skip till next token := *
     * 2 - Token is till ',' := <Value,,>
     * 3 - Token is till '' := <Value,; >
     * 4 - Token is till '.' := <Value,.>
     * 5 - Token is from end of  last" till ',' := <"R"Value,,>
     * 6 - Token is from end of  last" till '.' := <"R"Value,.>     * 
     */
    public void ruleParser() throws IOException {
        ruleReaders.iniRules();

        String[] readedHolder = ruleReaders.getTokenedRules();
        ruleLeng = readedHolder.length;
        ruleList = new Rule[ruleLeng];

        this.parseValues(ruleLeng, readedHolder);
        this.buildRuleList(readedHolder);
    }

    private void parseValues(int ruleLeng, String[] readedHolder) {
        valueArray = new String[ruleLeng];
        String tmpString;
                int begin = 0;
                int end = 0;
        for (int y = 0; y < ruleLeng; y++) {
            if(readedHolder[y].contains("@")){
                    begin = 1;
                    end = readedHolder[y].indexOf(",");
            }
            
            if (readedHolder[y].contains("*")) {
                if(readedHolder[y].contains(";")){
                    begin = 1;
                    end = readedHolder[y].indexOf(",");
                    tmpString = readedHolder[y].substring(begin, end);
                }else tmpString = "*";
            } else if (readedHolder[y].contains("\"")) {
                 begin = readedHolder[y].lastIndexOf("\"") + 1;
                 end = 0;

                if(readedHolder[y].contains(",;")){
                    end = readedHolder[y].lastIndexOf(",");
                }else if (readedHolder[y].contains(",,")) {
                    end = readedHolder[y].lastIndexOf(",") - 1;
                } else {
                    end = readedHolder[y].lastIndexOf(",");
                }                

               //System.out.println(begin + " " + end + " " +  readedHolder[y]);
                if(begin > end){
                  begin = 1;  
                }     
            tmpString = readedHolder[y].substring(begin, end);   
            }else {
                 begin = 1;
                 end = readedHolder[y].indexOf(",");
                tmpString = readedHolder[y].substring(begin, end);
            }
            //System.out.println(tmpString);
            valueArray[y] = tmpString;
        }
    }

    private String printRuleSet() {
        StringBuilder returnString = new StringBuilder();
        for (int y = 0; y < ruleList.length; y++) {
            returnString.append(" ").append(ruleList[y]);
        }
        return returnString.toString();
    }

    @Override
    public String toString() {
        return this.printRuleSet();
    }

    private void buildRuleList(String[] readedHolder) {
        for (int y = 0; y < valueArray.length; y++) {
            if(readedHolder[y].contains("@"))
                ruleList[y] = new Rule(10);
             if (readedHolder[y].contains("*")) {
                if(readedHolder[y].contains(";*"))
                    ruleList[y] = new Rule(9);
                else
                    ruleList[y] = new Rule(1);

            } else if (readedHolder[y].contains("\"R")) {
                if (readedHolder[y].contains(",.")) {
                    ruleList[y] = new Rule(6);
                } else if (readedHolder[y].contains(",,")) {
                    ruleList[y] = new Rule(5);
                } else if(readedHolder[y].contains(",;")) {
                     ruleList[y] = new Rule(8);
                }

            } else if (readedHolder[y].contains(",,")) {
                ruleList[y] = new Rule(2);

            } else if (readedHolder[y].contains(",;")) {
                ruleList[y] = new Rule(3);

            } else if (readedHolder[y].contains(",.")) {
                ruleList[y] = new Rule(4);

            }else if (readedHolder[y].contains("from")&& !readedHolder[y].contains("|") ){
                //System.out.println("FROM");
                ruleList[y] = new Rule(7);
            }
        }
    }
}