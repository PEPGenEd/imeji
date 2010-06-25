package de.mpg.escidoc.faces.container.collection;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.faces.album.list.util.AlbumListParameters;
import de.mpg.escidoc.faces.album.list.util.AlbumListParameters.OrderParameterType;
import de.mpg.escidoc.faces.album.list.util.AlbumListParameters.SortParameterType;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO.HandlerType;
import de.mpg.escidoc.services.framework.PropertyReader;

public class CollectionSession 
{
	private CollectionVO currentCollection = null;
	private FacesContainerListVO collectionList = null;
	
	public CollectionSession() 
	{
		
		
	}
	
	public void init() throws Exception
	{
		List<FacesContainerVO> list = new ArrayList<FacesContainerVO>();
		AlbumListParameters parameters = new AlbumListParameters("released", SortParameterType.LAST_MODIFICATION_DATE, OrderParameterType.DESCENDING, 10, 1, null, null);
		parameters.setContentModel(PropertyReader.getProperty("escidoc.faces.collection.content-model.id"));
		collectionList = new FacesContainerListVO(list, parameters, HandlerType.SEARCH);
	}
	
	
	public CollectionVO getCurrentCollection() 
	{
		return currentCollection;
	}

	public void setCurrentCollection(CollectionVO currentCollection) 
	{
		this.currentCollection = currentCollection;
	}

	public FacesContainerListVO getCollectionList() 
	{
		return collectionList;
	}

	public void setCollectionList(FacesContainerListVO collectionList) 
	{
		this.collectionList = collectionList;
	}



}