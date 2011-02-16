package de.mpg.imeji.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;

public class CollectionImagesBean extends ImagesBean {
	private int totalNumberOfRecords;
	private String id = null;
	private URI uri;
	private SessionBean sb;
	private CollectionImeji collection;
	private Navigation navigation;

	public CollectionImagesBean() {
		super();
		this.sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		this.navigation = (Navigation) BeanHelper
				.getApplicationBean(Navigation.class);
	}

	public void init() {
		CollectionController cc = new CollectionController(sb.getUser());
		this.collection = cc.retrieve(id);
	}

	@Override
	public String getNavigationString() {
		return "pretty:";
	}

	@Override
	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	@Override
	public List<ImageBean> retrieveList(int offset, int limit) throws Exception 
	{	
		uri = ObjectHelper.getURI(CollectionImeji.class, id);
		Collection<Image> images = new ArrayList<Image>();
		SortCriterion sortCriterion = new SortCriterion();
		sortCriterion.setSortingCriterion(ImejiNamespaces
				.valueOf(getSelectedSortCriterion()));
		sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
		List<List<SearchCriterion>> scList = new ArrayList<List<SearchCriterion>>();
		try {
			URLQueryTransformer queryTransformer = new URLQueryTransformer();
			scList = queryTransformer.transform(getQuery());
		} catch (Exception e) {
			BeanHelper.error("Invalid search query!");
		}
		CollectionController cc = new CollectionController(sb.getUser());
		totalNumberOfRecords = cc.getCollectionSize(uri.toString());
		ImageController controller = new ImageController(sb.getUser());
		images = controller.searchAdvancedInContainer(uri, scList, null, limit,
				offset);
		return ImejiFactory.imageListToBeanList(images);
	}

	public String getBackUrl() {
		return navigation.getImagesUrl() + "/collection" + "/" + this.id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCollection(CollectionImeji collection) {
		this.collection = collection;
	}

	public CollectionImeji getCollection() {
		return collection;
	}
}
