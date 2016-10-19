/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgaa_tanaguru;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author aallioui
 */
public class RGAA_glossaire {
    
    public static String glossaryUrl = "http://references.modernisation.gouv.fr/rgaa-accessibilite/glossaire.html";
    public static String glossaryTG = ""; // Chaîne de caractères qui contiendra le code HTML du glossaire
    
    /**
     * @param args
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * 
     * Création d'un fichier "glossaire_fr.html" avec le glossaire officiel reprenant le squelette du glossaire de Tanaguru
     * 
     */
    
    public static void main(String[] args) throws IOException, java.io.FileNotFoundException {
        
        /**
         * Parsage du glossaire officiel et récupération des informations nécessaires
         */

        // On parse la page du glossaire "officielle" 
        Document glossary = Jsoup.connect(glossaryUrl).get();
        
        // Titre de la page du référentiel officiel
        String glossaryTitle = glossary.select("title").first().text();
        
        // Nom de la version courante du RGAA
        String rgaaVersion = glossaryTitle.split("RGAA")[1];  
        
        // Titres de niveau 2 faisait référence à une lettre
        Elements titlesLetters = glossary.select("main h2");
        
        // Mois et année de mise en ligne
        String rgaaFullDate, rgaaDateMonth, rgaaDateYear;
        String[] months = {"janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre"};
        rgaaFullDate = glossary.select("p.version").first().text();
        rgaaFullDate = rgaaFullDate.split("publié le ")[1];
        rgaaFullDate = rgaaFullDate.split(" Note")[0];
        rgaaDateMonth  = months[Integer.parseInt(rgaaFullDate.split("/")[1]) - 1];
        rgaaDateYear = rgaaFullDate.split("/")[2];
          

        /**
         * Début de la page HTML du glossaire de Tanaguru
         */

        // Commentaires (version RGAA, url du fork)
        glossaryTG += "<!-- RGAA" + rgaaVersion + " - " + rgaaDateMonth + " " + rgaaDateYear + "\nSource : http://references.modernisation.gouv.fr/rgaa/" + "\nFork : https://github.com/Tanaguru/Rgaa_Website\n-->\n";
        // Doctype + balise title
        glossaryTG += "<!DOCTYPE html>\n<html lang=\"fr\">\n<head>\nmeta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n<title>" + glossaryTitle + "</title>\n";
        // Balises metas + liens css et jquery
        glossaryTG += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n<link rel=\"stylesheet\" href=\"../css/style.css\">\n<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js\"></script>\n</head>";
        // Ouverture du <body>, <header>
        glossaryTG += "<body>\n<a id=\"top\"></a>\n<header id=\"header\" role=\"banner\">\n<div class=\"headsite nav clearfix\">\n<strong id=\"logo\"><a href=\"index.html\">RGAA&nbsp;3.0 fr</a> | <a href=\"../en/\">en</a></strong>\n<button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#nav\" aria-controls=\"nav\" aria-expanded=\"false\" id=\"btnnav\">Menu</button>\n<nav id=\"nav\" class=\"collapse navbar-collapse\" role=\"navigation\">\n<ul><!-- --><li><strong class=\"on\">Critères</strong></li><!-- --><li><a href=\"./glossaire.html\">Glossaire</a></li><!-- --><li><a href=\"./cas-particuliers.html\">Cas particuliers</a></li><!-- --><li><a href=\"./notes-techniques.html\">Notes techniques</a></li><!-- --><li><a href=\"./base.html\">Base de référence</a></li><!-- --><li><a href=\"./references.html\">Références</a></li><!-- --><li><a href=\"./a-propos.html\">A propos</a></li><!-- --></ul>\n</nav>\n</div>\n";
        // Titre principal
        glossaryTG += "<div class=\"headrub page\">\n<h1>Glossaire</h1><p class=\"lead\">Le référentiel <abbr title=\"Référentiel Général d'Accessibilité des Administrations\">RGAA</abbr> fait référence à de nouveaux termes, de nouvelles définitions. À&nbsp;l'occasion de la parution officielle du référentiel RGAA, la liste des nouvelles définitions sera mise à jour si&nbsp;nécessaire et intégrée au glossaire RGAA.</p>\n";
        // Menu de navigation secondaire (par lettre) + fin du <header>
        glossaryTG += "<div id=\"navtoc\">\n<h2 id=\"toc\">Sommaire</h2>\n<nav class=\"nav\" role=\"navigation\" aria-labelledby=\"toc\">\n<ul>\n";
        for(Element title: titlesLetters) {
            String letterId = title.attr("id"), letterName = title.text();
            glossaryTG += "<li><a href=\"#" + letterId + "\">" + letterName + "</a></li>\n";
        }
        glossaryTG += "</ul>\n</nav>\n</div>\n</div>\n</header>\n";
        // Ouverture de la balise <main> + <div id="page">
        glossaryTG += "<main id=\"main\" role=\"main\">\n<div class=\"page\">\n";
        
        
        /** 
         * Contenu principal du glossaire (balise <main>)
         */
        
        // Récupération du code HTML dans la balise <main>
        Element glossaryContent = glossary.select("#main").first();
        // Suppression du titre h1 
        glossaryContent.select("h1").remove();
                
        /**
         * Ajout des "skiplinks" avec les titres h2
         */
    
        // Création de deux tableaus avec les "id" et les noms des lettres
        List<String>lettersId = new ArrayList<>();        
        List<String>lettersName = new ArrayList<>();
        for(Element letter: titlesLetters) {
            lettersId.add(letter.attr("id"));            
            lettersName.add(letter.text());
        }
                       
        int titleIndex = 0;
        
        for(Element title: titlesLetters) {  
            String skipLinks = "";
            if (titleIndex == 0) {
                skipLinks += "<p class=\"skiplink\"><span class=\"goafter\"><a href=\"#" + lettersId.get(titleIndex+1) + "\">Lettre&nbsp;" + lettersName.get(titleIndex+1) + "</a></span></p>\n";
            } else  {
                skipLinks +=  "<ul class=\"skiplink\">\n";
                if (titleIndex < titlesLetters.size() - 1) {
                    skipLinks += "<li class=\"gobefore\"><a href=\"#" + lettersId.get(titleIndex-1) + "\">Lettre&nbsp;" + lettersName.get(titleIndex-1) + "</a></li>\n";
                    skipLinks += "<li class=\"goafter\"><a href=\"#" + lettersId.get(titleIndex+1) + "\">Lettre&nbsp;" + lettersName.get(titleIndex+1) + "</a></li>\n";
                } else {
                    skipLinks += "<li class=\"gobefore\"><a href=\"#" + lettersId.get(titleIndex-1) + "\">Lettre&nbsp;" + lettersName.get(titleIndex-1) + "</a></li>";
                    skipLinks += "<li class=\"gobefore gotoc\"><a href=\"#toc\">Sommaire</a></li>";
                }
                skipLinks += "</ul>\n";
            }
            title.after(skipLinks);
            titleIndex++;
        } 
                 
        // Ajout du code HTML mis à jour dans la balise <main> et fermeture de celle-ci
        glossaryTG += glossaryContent.html();
        glossaryTG += "\n</div>\n</main>\n";
        
        
        /** 
         * Ajout du footer + scripts + fin de la page (</body></html>)
         */
    
        glossaryTG += "<footer id=\"footer\" role=\"contentinfo\">\n<div class=\"page\">";
        glossaryTG += "<h2>RGAA" + rgaaVersion + " - " + rgaaDateMonth + " " + rgaaDateYear + ".</h2>\n";
        glossaryTG += "<p>Ce document est un document de l'État placé sous <a href=\"https://www.etalab.gouv.fr/licence-ouverte-open-licence\" rel=\"external\" class=\"external\">licence ouverte&nbsp;1.0 ou ultérieure</a>, comme tous les documents hébergés sur <a href=\"http://references.modernisation.gouv.fr/\" rel=\"external\" class=\"external\">references.modernisation.gouv.fr</a>. Voir les conditions d'utilisation de la licence dans les <a href=\"http://references.modernisation.gouv.fr/mentions-legales\" rel=\"external\" class=\"external\">mentions légales</a>.</p><p>Le référentiel technique (liste des critères, glossaire, cas particuliers, notes techniques, base de référence) est une copie adaptée du <a href=\"http://www.accessiweb.org/index.php/accessiweb-html5aria-liste-deployee.html\" rel=\"external\" class=\"external\">référentiel AccessiWeb HTML5 / ARIA</a> - Version de travail du 19/12/2013 - Edité par l'association BrailleNet.</p><p>Cette page est réalisée avec <a href=\"http://tinytypo.tetue.net\" rel=\"external\" class=\"external\">Tiny Typo</a>, par <a href=\"http://www.tanaguru.com/fr/\" rel=\"external\" class=\"external\">Tanaguru</a>, un gentil dragon qui évalue votre accessibilité&nbsp;web.</p><p class=\"skiplink\"><span class=\"gobefore gotop\"><a href=\"#top\" title=\"Retour en haut de page\"><span>Haut de page</span></a></span></p>\n</div>\n</footer>\n";
        glossaryTG += "<script src=\"../js/main.js\"></script>\n<script>(function(d,e,j,h,f,c,b){d.GoogleAnalyticsObject=f;d[f]=d[f]||function(){(d[f].q=d[f].q||[]).push(arguments)},d[f].l=1*new Date();c=e.createElement(j),b=e.getElementsByTagName(j)[0];c.async=1;c.src=h;b.parentNode.insertBefore(c,b)})(window,document,\"script\",\"https://www.google-analytics.com/analytics.js\",\"ga\");ga(\"create\",\"UA-76056657-1\",\"auto\");ga(\"send\",\"pageview\");</script>";
        glossaryTG += "\n</body>\n</html>";
        
        /**
         * Création d'un fichier contenant le code HTML du glossaire
         * (Code à faire passer dans un HTML formatter pour l'indentation : http://www.freeformatter.com/html-formatter.html) 
         */
        try(  PrintWriter out = new PrintWriter( "glossaire_fr.html" )  ){
            out.println( glossaryTG );
        }
    }
    
    
}
