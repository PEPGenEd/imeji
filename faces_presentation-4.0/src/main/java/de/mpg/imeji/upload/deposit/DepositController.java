package de.mpg.imeji.upload.deposit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import de.escidoc.schemas.item.x09.ItemDocument;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.escidoc.ItemVO;
import de.mpg.imeji.upload.helper.ImageHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Image.Visibility;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.User;

/**
 * @author yu
 */
public class DepositController
{
    public static ItemVO createImejiItem(InputStream inputStream, String title, String description,
            String mimetype, String format, String userHandle, String collection, String context) throws IOException, URISyntaxException
    {
        ItemVO imejiItem = new ItemVO(title, description, context);
        try
        {
            imejiItem.attachFile(inputStream, title, mimetype, format, userHandle);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return imejiItem;
    }

    public static String depositImejiItem(ItemVO item, String userHandle, CollectionImeji collection, User user, String title) throws Exception
    {
        String itemXml = ServiceLocator.getItemHandler(userHandle).create(item.getItemDocument().xmlText());
        item.setItem(ItemDocument.Factory.parse(itemXml));
        
        ImageController imageController = new ImageController(user);
        Image img = new Image();        
        img.setCollection(collection.getId());
        img.setFullImageUrl(URI.create(ImageHelper.getOriginalResolution(item)));
        img.setThumbnailImageUrl(URI.create(ImageHelper.getThumbnailUrl(item)));
        img.setWebImageUrl(URI.create(ImageHelper.getWebResolutionUrl(item)));
        img.setVisibility(Visibility.PUBLIC);
        img.setFilename(title);
        img.setEscidocId(item.getItemDocument().getItem().getObjid());
        if(collection.getProperties().getStatus() == Status.RELEASED)
        	img.getProperties().setStatus(Status.RELEASED);
        imageController.create(img, collection.getId());

        
//        String taskParam = "<param last-modification-date=\""
//                + item.getItemDocument().getItem().getLastModificationDate()
//                + "\"><comment>Submit for upload.</comment></param>";
//        String lastModificationDate = ServiceLocator.getItemHandler(userHandle).submit(
//                item.getItemDocument().getItem().getObjid(), taskParam);
//        ResultDocument rdoc = ResultDocument.Factory.parse(lastModificationDate);
//        taskParam = "<param last-modification-date=\"" + rdoc.getResult().getLastModificationDate() + "\"><url>"
//                + ServiceLocator.getFrameworkUrl() + "ir/item/" + item.getItemDocument().getItem().getObjid()
//                + "</url></param>";
//        lastModificationDate = ServiceLocator.getItemHandler(userHandle).assignObjectPid(
//                item.getItemDocument().getItem().getObjid(), taskParam);
//        rdoc = ResultDocument.Factory.parse(lastModificationDate);
//        taskParam = "<param last-modification-date=\"" + rdoc.getResult().getLastModificationDate() + "\"><url>"
//                + ServiceLocator.getFrameworkUrl() + "ir/item/" + item.getItemDocument().getItem().getObjid()
//                + ":1</url></param>";
//        lastModificationDate = ServiceLocator.getItemHandler(userHandle).assignVersionPid(
//                item.getItemDocument().getItem().getObjid(), taskParam);
//        rdoc = ResultDocument.Factory.parse(lastModificationDate);
//        taskParam = "<param last-modification-date=\"" + rdoc.getResult().getLastModificationDate()
//                + "\"><comment>Release for upload.</comment></param>";
//        lastModificationDate = ServiceLocator.getItemHandler(userHandle).release(
//                item.getItemDocument().getItem().getObjid(), taskParam);
        return itemXml;
    }
    
    public static void main(String[] args) throws Exception {
		URI uri = new URI("");
	}
}
