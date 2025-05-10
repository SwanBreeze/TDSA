const cacheName = 'v2'; 

const cachedAssets = [
    'sidePanel.html',
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
    'search_icon_cropped_192x192.png',
    'search_icon_cropped_512x512.png',
    'ustoLogo.png',
    'PDF.png',
    'DOCX.png',
    'EXCEL.png'
];

// install
self.addEventListener('install', event => {
    console.log('Service Worker: Installed');

    event.waitUntil(
        caches.open(cacheName).then(cache => {
            console.log('Service Worker: Caching Files');
            return cache.addAll(cachedAssets);
        }).then(() => self.skipWaiting())
    );
});

// activate
self.addEventListener('activate', event => {
    console.log('Service Worker: Activated');

    // to remove old caches
    event.waitUntil(
        caches.keys().then(cacheNames => {
            return Promise.all(
                cacheNames.map(name => {
                    if (name !== cacheName) {
                        console.log('Service Worker: Clearing Old Cache');
                        return caches.delete(name);
                    }
                })
            );
        }).then(() => self.clients.claim())
    );
});

// fetch
self.addEventListener('fetch', event => {
    console.log('Service Worker: Fetching', event.request.url);

    event.respondWith(
        fetch(event.request).catch(() => caches.match(event.request))
    );
});

// push notification
self.addEventListener('push', event => {
    const data = event.data.json();
    self.registration.showNotification(data.title, {
        body: data.body,
        icon: 'search_icon_cropped_192x192.png',
    });
});
