
import { setupNavigation, fadeLoad, updateNavbarVisibility } from './utility.js';


const pageScriptRegistry = {
    'registration.html': () => import('./registration.js').then(mod => mod.initRegistrationForm()),

    'login.html': () => import('./login.js').then(mod => mod.initLogin()),

    'products.html': () => import('./product.js').then(mod => mod.initProducts()),

    'addProduct.html': () => import('./productAdd.js').then(mod =>mod.initProductAdd()),

    'editProduct.html': () => import('./productEdit.js').then(mod =>{
        mod.initProductEdit();
    }),

    'editProductIn.html': () => import('./productEditIn.js').then(mod =>{
        mod.initProductEditIn();
    }),

    'update.html': () => import('./update.js').then(mod => {
        mod.initUpdate();
    }),

    'shoppingList.html': () => import('./shoppingList.js').then(mod =>{
        mod.initShoppingList();
    }),

    'users.html': () => import('./users.js').then(mod => {
        mod.initUsers()
    }),
};

$(document).ready(function () {
    setupNavigation('#main-content');
    fadeLoad('#main-content', 'home.html');
    updateNavbarVisibility();
});

window.runPageScript = function(pageUrl) {
    const cleanUrl = pageUrl.split('?')[0];
    if (pageScriptRegistry[cleanUrl]) {
        pageScriptRegistry[cleanUrl]();
    }
}