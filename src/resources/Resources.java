package resources;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

/**
 * Created by Cliff on 6/16/17.
 *
 * IMPORTANT: Do not modify this class! See AVAsset.java for documentation
 * See AV
 */
public class Resources {

    private static MediaPlayer nowPlaying = null;

    private Resources() {
        //private constructor
    }

    private static Image getImage(AVAsset asset) {

        //full filePath
        String filePath = "images/" + asset + ".png";

        URL resource = Resources.class.getResource(
                filePath);

        if (resource == null) {
            System.out.printf("\nResource for name '%s' does not exist!\n",
                    asset);
            return null;
        } else {
            return new Image(resource.toExternalForm());
        }

    }

    /**
     * Call this method to return an ImageView containing an image asset
     * Important: Declare the image asset in AVAsset.java first.
     *
     * @param asset the file name of the asset to be retrieved, which must be
     *              declared in the AVAsset enum WITHOUT its MIME identifier.
     *              For example, 'AppIcon.png' should be added to the AVAsset
     *              enum list as 'AppIcon'
     *              Only .png image files will work.
     * @param width the desired width of the returned ImageView. The aspect
     *              ratio of the image will be conserved
     *
     * @return an ImageView containing either an Image of the asset or null,
     * indicating that the AVAsset name was invalid.
     */
    public static ImageView imageViewFor(AVAsset asset, double width) {

        Image image = getImage(asset);
        ImageView imageView = new ImageView(image);

        //Resize and resample every ImageView's icon image
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        return imageView;
    }


    public static void playAudio(AVAsset soundAsset) {

        if (nowPlaying != null) {
            nowPlaying.stop();
            //Stop last sound from continuing to play
        }

        //full filePath
        String filePath = "sounds/" + soundAsset + ".aiff";

        nowPlaying = new MediaPlayer(new Media(Resources.class
            .getResource(filePath).toExternalForm()));

        nowPlaying.play();
    }

}
