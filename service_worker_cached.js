const cacheName = 'v1';

const cachedAssets = [
    'index.html',
    'searchBar.html',
    'Upload.html',
    'cssGlobale.css',
    'search.css',
    'upload.css',
    'app.js',
    'search.js',
    'upload.js',
    'login.html',
    'login.css',
    'android-chrome-192.png',
    'android-chrome-512.png',
    'ustoLogo.png',
    'PDF.png',
    'DOCX.png',
    'EXCEL.png'
    
]

//install
self.addEventListener('install', event =>{
    console.log('Service Worker: Installed')


    event.waitUntil(
        caches
        .open(cacheName)
        .then((cache) =>{
            console.log('Service Worker: Caching Files')
            cache.addAll(cachedAssets);
        })
        .then(()=> self.skipWaiting())
    );
});

//activate to add later on
self.addEventListener('activate', event =>{
    console.log('Service Worker: Activated');
});

self.addEventListener('fetch', event =>{
    console.log('Service Worker: Fetching');

    event.respondWith(fetch(event.request)
     .catch(()=>{
       return caches.match(event.request)
     })
)
})

