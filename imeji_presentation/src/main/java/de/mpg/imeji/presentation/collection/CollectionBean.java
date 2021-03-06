/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.security.Authorization;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.image.ImageBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.UrlHelper;

public abstract class CollectionBean
{
    public enum TabType
    {
        COLLECTION, PROFILE, HOME;
    }

    private static Logger logger = Logger.getLogger(CollectionBean.class);
    private TabType tab = TabType.HOME;
    private SessionBean sessionBean = null;
    private CollectionImeji collection = null;
    private MetadataProfile profile = null;
    private String id = null;
    private int authorPosition;
    private int organizationPosition;
    private List<SelectItem> profilesMenu = new ArrayList<SelectItem>();
    private boolean selected;
    private int size = 0;

    public CollectionBean(CollectionImeji coll)
    {
        this.collection = coll;
        // setId(ObjectHelper.getId(coll.getId()));
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    public CollectionBean()
    {
        collection = new CollectionImeji();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    public boolean valid()
    {
        if (collection.getMetadata().getTitle() == null || "".equals(collection.getMetadata().getTitle()))
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_need_title"));
            return false;
        }
        List<Person> pers = new ArrayList<Person>();
        for (Person c : collection.getMetadata().getPersons())
        {
            List<Organization> orgs = new ArrayList<Organization>();
            for (Organization o : c.getOrganizations())
            {
                if (!"".equals(o.getName()))
                {
                    orgs.add(o);
                }
            }
            if (!"".equals(c.getFamilyName()))
            {
                if (orgs.size() > 0)
                {
                    c.setOrganizations(orgs);
                    pers.add(c);
                }
                else
                {
                    BeanHelper.error(sessionBean.getMessage("error_author_need_one_organization"));
                    return false;
                }
            }
        }
        if (pers.size() == 0)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_need_one_author"));
            return false;
        }
        collection.getMetadata().setPersons(pers);
        return true;
    }

    public String addAuthor()
    {
        List<Person> c = (List<Person>)collection.getMetadata().getPersons();
        Person p = ImejiFactory.newPerson();
        p.setPos(authorPosition + 1);
        c.add(authorPosition + 1, p);
        return getNavigationString();
    }

    public String removeAuthor()
    {
        List<Person> c = (List<Person>)collection.getMetadata().getPersons();
        if (c.size() > 1)
            c.remove(authorPosition);
        else
            BeanHelper.error(sessionBean.getMessage("error_collection_need_one_author"));
        return getNavigationString();
    }

    public String addOrganization()
    {
        List<Person> persons = (List<Person>)collection.getMetadata().getPersons();
        List<Organization> orgs = (List<Organization>)persons.get(authorPosition).getOrganizations();
        Organization o = ImejiFactory.newOrganization();
        o.setPos(organizationPosition + 1);
        orgs.add(organizationPosition + 1, o);
        return getNavigationString();
    }

    public String removeOrganization()
    {
        List<Person> persons = (List<Person>)collection.getMetadata().getPersons();
        List<Organization> orgs = (List<Organization>)persons.get(authorPosition).getOrganizations();
        if (orgs.size() > 1)
            orgs.remove(organizationPosition);
        else
            BeanHelper.error(sessionBean.getMessage("error_author_need_one_organization"));
        return getNavigationString();
    }

    protected abstract String getNavigationString();

    public int getAuthorPosition()
    {
        return authorPosition;
    }

    public void setAuthorPosition(int pos)
    {
        this.authorPosition = pos;
    }

    /**
     * @return the collectionPosition
     */
    public int getOrganizationPosition()
    {
        return organizationPosition;
    }

    /**
     * @param collectionPosition the collectionPosition to set
     */
    public void setOrganizationPosition(int organizationPosition)
    {
        this.organizationPosition = organizationPosition;
    }

    /**
     * @return the tab
     */
    public TabType getTab()
    {
        if (UrlHelper.getParameterValue("tab") != null)
        {
            tab = TabType.valueOf(UrlHelper.getParameterValue("tab").toUpperCase());
        }
        return tab;
    }

    /**
     * @param tab the tab to set
     */
    public void setTab(TabType tab)
    {
        this.tab = tab;
    }

    /**
     * @return the collection
     */
    public CollectionImeji getCollection()
    {
        return collection;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    public List<SelectItem> getProfilesMenu()
    {
        return profilesMenu;
    }

    public void setProfilesMenu(List<SelectItem> profilesMenu)
    {
        this.profilesMenu = profilesMenu;
    }

    /**
     * @return the selected
     */
    public boolean getSelected()
    {
        if (sessionBean.getSelectedCollections().contains(collection.getId()))
            selected = true;
        else
            selected = false;
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected)
    {
        if (selected)
        {
            if (!(sessionBean.getSelectedCollections().contains(collection.getId())))
                sessionBean.getSelectedCollections().add(collection.getId());
        }
        else
            sessionBean.getSelectedCollections().remove(collection.getId());
        this.selected = selected;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getSize()
    {
        return size;
    }

    public boolean getIsOwner()
    {
        if (collection != null && collection.getCreatedBy() != null && sessionBean.getUser() != null)
        {
            return collection.getCreatedBy().equals(ObjectHelper.getURI(User.class, sessionBean.getUser().getEmail()));
        }
        return false;
    }

    public String release()
    {
        CollectionController cc = new CollectionController(sessionBean.getUser());
        try
        {
            cc.release(collection, sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_collection_release"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_release"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "pretty:";
    }

    public String delete()
    {
        CollectionController cc = new CollectionController(sessionBean.getUser());
        try
        {
            cc.delete(collection, sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_collection_delete"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_delete"));
            logger.error("Error delete collection", e);
        }
        return "pretty:collections";
    }

    public String withdraw() throws Exception
    {
        CollectionController cc = new CollectionController(sessionBean.getUser());
        try
        {
            cc.withdraw(collection);
            BeanHelper.info(sessionBean.getMessage("success_collection_withdraw"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_withdraw"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "pretty:";
    }

    public List<ImageBean> getImages() throws Exception
    {
        ItemController ic = new ItemController(sessionBean.getUser());
        if (collection == null || collection.getId() == null)
            return null;
        try
        {
            Search search = new Search(null, collection.getId().toString());
            List<String> uris = search.searchSimpleForQuery(
                    "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT ?s WHERE { ?s <http://imeji.org/terms/collection> <"
                            + collection.getId().toString()
                            + "> . ?s <http://imeji.org/terms/status> ?status   .FILTER(?status!=<"
                            + Status.WITHDRAWN.getUri() + ">)} LIMIT 5", new SortCriterion());
            return ImejiFactory.imageListToBeanList(ic.loadItems(uris, 5, 0));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public MetadataProfile getProfile()
    {
        return profile;
    }

    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }

    public boolean isEditable()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sessionBean.getUser(), collection);
    }

    public boolean isVisible()
    {
        Security security = new Security();
        return security.check(OperationsType.READ, sessionBean.getUser(), collection);
    }

    public boolean isDeletable()
    {
        Security security = new Security();
        return security.check(OperationsType.DELETE, sessionBean.getUser(), collection);
    }

    public boolean isProfileEditor()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sessionBean.getUser(), profile);
    }

    public boolean isAdmin()
    {
        Authorization auth = new Authorization();
        return auth.isContainerAdmin(sessionBean.getUser(), collection);
    }
}
