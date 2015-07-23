package operate.rbac;

import play.Logger;
import play.Play;
import play.Play.Mode;
import play.PlayPlugin;
import play.db.jpa.JPAPlugin;
import play.vfs.VirtualFile;

import java.util.LinkedList;
import java.util.List;


/**
 * Initialize and reload the navigation structure.
 * 
 */
public class NavigationPlugin extends PlayPlugin {

    // Timestamp the navigation was last loaded
    long lastLoaded = -1;
    List<VirtualFile> navigationFiles;

    boolean reloadRbac = Boolean.valueOf(Play.configuration.getProperty("rbac.reload", "false"));
    
    @Override
    public void afterApplicationStart() {
        navigationFiles = new LinkedList<VirtualFile>();
        navigationFiles.add(VirtualFile.fromRelativePath("conf/rbac.xml"));
        detectChange();
    }

    @Override
    public void detectChange() {
        //生产模式下只会运行一次
        if (Play.mode == Mode.PROD && lastLoaded > 0) {
            return;
        }

        if (Play.mode == Mode.DEV && !reloadRbac) {
            return;
        }

        JPAPlugin.startTx(false);
        try {
            boolean reload = false;
            for (VirtualFile navigationFile : navigationFiles) {
                if (navigationFile.lastModified() > lastLoaded) {
                    lastLoaded = navigationFile.lastModified();
                    reload = true;
                }
            }

            if (reload) {
                Logger.info("Reloading navigation file.. lastLoaded=" + lastLoaded);
                // TODO: Support multiple files
                RbacLoader.init(navigationFiles.get(0));
                
                // 初始化菜单名称
                NavigationHandler.initNamedMenus();
            }
        } finally {
            JPAPlugin.closeTx(false);
        }
    }

    @Override
    public void beforeInvocation() {
        NavigationHandler.clearMenuContext();
    }
}
