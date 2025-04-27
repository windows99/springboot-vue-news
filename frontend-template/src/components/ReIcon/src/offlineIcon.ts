import { addIcon } from "@iconify/vue/dist/offline";

/**
 * 这里存放本地图标，在 src/layout/index.vue 文件中加载，避免在首启动加载
 */

// 本地菜单图标，后端在路由的icon中返回对应的图标字符串并且前端在此处使用addIcon添加即可渲染菜单图标
import HomeFilled from "@iconify-icons/ep/home-filled";
import InformationLine from "@iconify-icons/ri/information-line";
import Lollipop from "@iconify-icons/ep/lollipop";
import Memo from "@iconify-icons/ep/memo";
import DocumentAdd from "@iconify-icons/ep/document-add";
import Bell from "@iconify-icons/ep/bell";
import Files from "@iconify-icons/ep/files";
import Setting from "@iconify-icons/ep/setting";

addIcon("homeFilled", HomeFilled);
addIcon("memo", Memo);
addIcon("informationLine", InformationLine);
addIcon("lollipop", Lollipop);
addIcon("documentAdd", DocumentAdd);
addIcon("bell", Bell);
addIcon("files", Files);
addIcon("setting", Setting);
