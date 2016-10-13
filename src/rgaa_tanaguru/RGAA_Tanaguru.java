/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgaa_tanaguru;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author tanaguru
 */
public class RGAA_Tanaguru {

    public static String rgaaOfficialUrl = "http://references.modernisation.gouv.fr/";
    public static String refTanaguruUrl = "http://rgaa.tanaguru.com/fr/criteres.html";    
    public static String rgaaVersion;
    public static String refOfficialTitle;
    public static Document refOfficial;
    public static String refTGContent;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        // On parse la page du référentiel officiel
        refOfficial = Jsoup.connect(rgaaOfficialUrl + "rgaa-accessibilite/criteres.html").get(); 
       
        // Titre de la page du référentiel officiel
        refOfficialTitle = refOfficial.select("title").first().text();
        
        // Récupération du nom de la version courante du RGAA
        rgaaVersion = refOfficialTitle.split("RGAA")[1];  
        
        // Récupération du mois et année de mise en ligne
        String rgaaFullDate, rgaaDateMonth, rgaaDateYear;
        String[] months = {"janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre"};
        
        rgaaFullDate= refOfficial.select("p.version").first().text();
        rgaaFullDate = rgaaFullDate.split("publié le ")[1];
        rgaaFullDate = rgaaFullDate.split(" Note")[0];
        rgaaDateMonth  = months[Integer.parseInt(rgaaFullDate.split("/")[1]) - 1];
        rgaaDateYear = rgaaFullDate.split("/")[2];
          
        // Début de la page du référentiel Tanaguru
        // Va du header jusqu'au premier lien "Mode d'emploi" de la navigation 
        refTGContent = "<!-- RGAA" + rgaaVersion + " - " + rgaaDateMonth + " " + rgaaDateYear + " Source : " + rgaaOfficialUrl + "rgaa-accessibilite/criterions.html" + " Fork : https://github.com/Tanaguru/Rgaa_Website --> <!DOCTYPE html> <html lang=\"fr\"> <head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /> <title>" + refOfficialTitle + "</title> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> <link rel=\"stylesheet\" href=\"../css/style.css\"> <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js\"></script> </head> <body> <a name=\"top\" id=\"top\"></a> <header id=\"header\" role=\"banner\"> <div class=\"headsite nav clearfix\"> <strong id=\"logo\"><a href=\"index.html\">RGAA&nbsp;3.0 fr</a> | <a href=\"../en/\">en</a></strong> <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#nav\" aria-controls=\"nav\" aria-expanded=\"false\" id=\"btnnav\">Menu</button> <nav id=\"nav\" class=\"collapse navbar-collapse\" role=\"navigation\"> <ul><!-- --><li><strong class=\"on\">Critères</strong></li><!-- --><li><a href=\"./glossaire.html\">Glossaire</a></li><!-- --><li><a href=\"./cas-particuliers.html\">Cas particuliers</a></li><!-- --><li><a href=\"./notes-techniques.html\">Notes techniques</a></li><!-- --><li><a href=\"./base.html\">Base de référence</a></li><!-- --><li><a href=\"./references.html\">Références</a></li><!-- --></ul> </nav> </div> <div class=\"headrub page\"> <h1>Critères</h1> <div id=\"navtoc\"> <h2 id=\"filtres\">Filtres</h2> <div aria-labelledby=\"filtres\"> <div class=\"filter\"> <input type=\"checkbox\" id=\"A\" checked=\"checked\" aria-checked=\"true\"> <label for=\"A\">Niveau A</label> </div> <div class=\"filter\"> <input type=\"checkbox\" id=\"AA\" checked=\"checked\" aria-checked=\"true\"> <label for=\"AA\">Niveau AA</label> </div> <div class=\"filter\"> <input type=\"checkbox\" id=\"AAA\" checked=\"checked\" aria-checked=\"true\"> <label for=\"AAA\">Niveau AAA</label> </div> </div> <h2 id=\"toc\">Sommaire</h2> <nav class=\"nav\" role=\"navigation\" aria-labelledby=\"toc\"> <ul> <li class=\"on\"><a href=\"#howto\">Mode d'emploi</a></li>";
        
        // Récupération des ancres vers les thématiques
        Elements thematicsLinks = refOfficial.select("#nav-thematiques a");
                
        // Ajout des ancres vers les thématiques 
        refTGContent += getThematicsAnchors(thematicsLinks);
        
        // Ajout de la section "Mode d'emploi" 
        refTGContent += "</ul> </nav> </div> </div> </header> <main id=\"main\" role=\"main\"> <div class=\"page\"> <div id=\"howto\" class=\"thematique\"><h2>Mode d'emploi</h2>";
        refTGContent += getSectionHowTo() + "</div>";
        
        // Ajout des thématiques, des critères associés et des tests 
        refTGContent += getThematicsContent(thematicsLinks) + "</div></main>";
        
        // Ajout du bas de page : footer/crédit/etc. + bouton "retour au top"
        refTGContent += getFooter() + "</body></html>";
        
        // Affichage en console du code HTML final
        System.out.println(refTGContent);
    }

    private static String getThematicsAnchors(Elements thematicsLinks) throws IOException {
        
        String thematicsLinksHTML = "";
        
        int i = 0;
        
        for(Element thematicLink : thematicsLinks) {
                                  
           String nomTheme = thematicLink.text();
           int indexTheme = i + 1;
           
           thematicLink.text(indexTheme + ". " + nomTheme);
           
           thematicsLinksHTML += "<li>" + thematicLink + "</li>";
           
           i++;
        }
                
        return thematicsLinksHTML;
    }
    
    private static String getSectionHowTo() throws IOException {
        
        Element modeEmploiSection = refOfficial.select("#display-guide").first();
        
        // Mise à jour des url des liens dans la section "Mode d'emploi" 
        Elements links = modeEmploiSection.select("a[href$='.html']");
        
        for (Element link : links) {
            link.attr("href", rgaaOfficialUrl + link.attr("href"));
        }
        
        return modeEmploiSection.html();
    }

    private static String getThematicsContent(Elements thematicsLinks) throws IOException {
        
        String thematicsDatas = "";
        
        Elements thematics = refOfficial.select("main section");
        
        int thematicIndex = 0;
                
        for (Element thematic : thematics) {
            String thematicId = thematic.attr("id"); 
            String thematicTitre = thematic.select("h2").first().text();
            String thematicPrincipes = thematic.select(".principe").first().text().split(": ")[1];
            String thematicRecommandation = thematic.select(".reco + p").first().html();

            // En tête d'une "Thématique" (balise< <article> + <header> )
            
            thematicsDatas += "<article id=\"" + thematicId +  "\" class=\"thematique\">";
            thematicsDatas += "<header><h2>" + thematicTitre + "</h2>";
            
            // Skiplinks
            
            if (thematicIndex > 0) 
                thematicsDatas += "<ul class=\"skiplink\">";
            else 
                thematicsDatas += "<p class=\"skiplink\">";
            
            if (thematicIndex > 0) {
                thematicsDatas += "<li class=\"gobefore gotoc\"><a href=\"#toc\">Sommaire</a></li>";
                
                Element previousThematic = thematicsLinks.get(thematicIndex-1);
                String previousThematicTitle = previousThematic.text().split(" ")[1];
                thematicsDatas += "<li class=\"gobefore\"><a href=\"" + previousThematic.attr("href") + "\">" + previousThematicTitle + "</a></li>";
            }
                        
            if (thematicIndex < thematicsLinks.size() - 1) {
                Element nextThematic = thematicsLinks.get(thematicIndex+1);
                String nextThematicTitle = nextThematic.text().split(" ")[1];
                String nextThematicLink = "<a href=\"" + nextThematic.attr("href") + "\">" + nextThematicTitle + "</a>";
                
                if (thematicIndex == 0)
                    thematicsDatas += nextThematicLink;
                else 
                    thematicsDatas += "<li class=\"goafter\">" + nextThematicLink + "</li>";

            }
            
            if (thematicIndex > 0) 
                thematicsDatas += "</ul>";
            else 
                thematicsDatas += "</p>";
            
                        
            // Récupérations des critères et des tests associés pour chaque thématique
            ArrayList thematicCriterions = getCriterionsAndTests(thematic, thematicPrincipes);
            
            // Créations des ancres vers les critères de la thématique

            thematicsDatas += "<nav><ul>";

            for (int i = 0; i < thematicCriterions.size(); i++) {
                HashMap<String,String> criterion = (HashMap<String,String>) thematicCriterions.get(i);
                String criterionLevel = criterion.get("level");
                String criterionIdAnchor = criterion.get("id");
                String criterionIdText = criterionIdAnchor.replace("-", ".").split("crit.")[1];
                
                thematicsDatas += "<li><a data-level=\"" + criterionLevel +  "\" href=\"#" + criterionIdAnchor + "\">" + criterionIdText + "</a></li>";
            }
            
            thematicsDatas += "</ul></nav>";
            
            // Recommandation liée à la thématique
            thematicsDatas += "<h3>Recommandation</h3><p>" + thematicRecommandation + "</p>";
            
            // Principes liées à la thématique
            thematicsDatas += "<div class=\"tags\"><span>Mots clés</span><ul><li>Principe : " + thematicPrincipes + "</li></ul></div>";
            
            // Fin de l'en-tête / header de la thématique
            thematicsDatas += "</header>";
            
            // Ajout des critères et de leur tests
            for (int i = 0; i < thematicCriterions.size(); i++) {
                HashMap<String,String> criterion = (HashMap<String,String>) thematicCriterions.get(i);
                String criterionLevel = criterion.get("level");
                String criterionIdAnchor = criterion.get("id");            
                String criterionIdText = criterionIdAnchor.replace("-", ".").split("crit.")[1];
                String criterionTitlePart1 = criterion.get("titrePartie1");               
                String criterionTitlePart2 = criterion.get("titrePartie2");
                String criterionTests = criterion.get("testsList");
                String criterionCorrespondance = criterion.get("correspondances");

                thematicsDatas += "<section data-level=\"" + criterionLevel + "\">";
                thematicsDatas += "<h3 id=\"" + criterionIdAnchor + "\"><em>" + criterionTitlePart1 + "</em>" + criterionTitlePart2 + "</h3>";
                thematicsDatas += "<ul>" + criterionTests + "</ul>";
                thematicsDatas += "<aside>" + criterionCorrespondance + "</aside>";
                thematicsDatas += "</section>";
            }
            
            thematicsDatas += "</article>";

            thematicIndex++;
            
        }   
                
        return thematicsDatas;
    }

    private static ArrayList getCriterionsAndTests(Element thematic, String thematicPrincipes) throws IOException {
        
        ArrayList thematicCriterionsDatas = new ArrayList();
        
        // Récupérer l'ensemble des critères
        Elements criterions = thematic.select("article");
        
        for (Element criterion : criterions) {
                        
            // Id
            String criterionId = criterion.attr("id");
            
            // Titre
            String criterionTitle = criterion.select("h3").first().text();
            String criterionTitlePart1 = criterionTitle.split("]")[0] + "]";
            String criterionTitlePart2 = criterionTitle.split("]")[1];
            
            // Niveau du critère
            String criterionLevel = criterion.attr("data-level");
            
            // Liste de tous les tests associés au critère
            String criterionTestsList = criterion.select("ul").first().html();
            
            // Bloc "Correspondances WCAG 2.X"
            Element criterionCorrespondance = criterion.select("aside").first();
            
            // Mise à jour des liens dans cette section
            Elements correspondanceLinks = criterionCorrespondance.select("a");
            for (Element link : correspondanceLinks) {
                String linkText = link.text();
                
                link.attr("rel", "external")
                    .attr("hreflang", "en")
                    .attr("title", linkText + " (nouvelle fenêtre)")                
                    .attr("target", "_blank");
            }
            // Ajout des tags : niveau et principes
            String criterionCorrespondanceAside = "<div class=\"tags\"><span>Mot-clés</span><ul><li>Niveau : " + criterionLevel + "</li><li>Principe : "+ thematicPrincipes +"</li></ul></div>";
            criterionCorrespondance.html(criterionCorrespondance.html() + criterionCorrespondanceAside);
            String criterionCorrespondanceHTML = criterionCorrespondance.html();
            
            Map criterionDatas = new HashMap<>();

            criterionDatas.put("id", criterionId);
            criterionDatas.put("level", criterionLevel);
            criterionDatas.put("titrePartie1", criterionTitlePart1);
            criterionDatas.put("titrePartie2", criterionTitlePart2);
            criterionDatas.put("testsList", criterionTestsList);
            criterionDatas.put("correspondances", criterionCorrespondanceHTML);
         
            thematicCriterionsDatas.add(criterionDatas);
        }
        
        return thematicCriterionsDatas;
    }
    
    public static String getFooter() throws IOException {
        
        String pageFooter    = "", 
               footerContent = "";
        
        Element footer = refOfficial.select("footer").first();
        
        // Suppression du logo
        footer.select("img").remove();
        
        // Mise à jour des liens avec l'URL du référentiel officiel
        Elements footerLinks = footer.select("a");
        
        for(Element link : footerLinks) {
            String linkURL = link.attr("href");
            if (linkURL.indexOf("../") > -1) {
                linkURL = linkURL.replace("../", rgaaOfficialUrl);
                link.attr("href", linkURL);
            }
        }
                
        footerContent = footer.html().split("</h2>")[1];  
        footerContent = footerContent.split("<hr>")[0];
        footerContent += "<p>Cette page est réalisée avec <a href=\"http://tinytypo.tetue.net\" rel=\"external\" class=\"external\">Tiny Typo</a>, par <a href=\"http://www.tanaguru.com/fr/\" rel=\"external\" class=\"external\">Tanaguru</a>, un gentil dragon qui évalue votre accessibilité&nbsp;web.</p>";
        footerContent += "<p class=\"skiplink\"><span class=\"gobefore gotop\"><a href=\"#top\" title=\"Retour en haut de page\"><span>Haut de page</span></a></span></p>";
        
        pageFooter += "<footer id=\"footer\" role=\"contentinfo\"><div class=\"page\">";
        pageFooter += "<h2>RGAA" + rgaaVersion + "</h2>";
        pageFooter += footerContent;
        pageFooter += "</div></footer>";        
        pageFooter += "<script src=\"../js/main.js\"></script>";
                
        return pageFooter;
    }
    
}
