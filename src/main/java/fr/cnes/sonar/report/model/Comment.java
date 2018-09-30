/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.model;

/**
 * A simple comment on an issue.
 */
public class Comment {

    /** Comment's key. */
    private String key;
    /** Comment's author login. */
    private String login;
    /** Comment's HTML text. */
    private String htmlText;
    /** Comment's markdown text. */
    private String markdown;
    /** Comment's updatable status. */
    private boolean updatable;
    /** Comment's creation date. */
    private String createdAt;

    /**
     * Complete constructor.
     */
    public Comment(){
        this.key = "";
        this.login = "";
        this.htmlText = "";
        this.markdown = "";
        this.updatable = false;
        this.createdAt = "";
    }

    /**
     * Getter for key.
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Getter for login.
     * @return login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Getter for htmlText.
     * @return htmlText
     */
    public String getHtmlText() {
        return htmlText;
    }

    /**
     * Getter for markdown.
     * @return markdown
     */
    public String getMarkdown() {
        return markdown;
    }

    /**
     * Getter for updatable.
     * @return updatable
     */
    public boolean isUpdatable() {
        return updatable;
    }

    /**
     * Getter for createdAt.
     * @return createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }
}