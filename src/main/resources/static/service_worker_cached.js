const cacheName = 'v1';

const cachedAssets = [
    'sidePanel.html',
    'searchBar.html',
    'upload.html',
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
self.addEventListener('install', event => {
    console.log('Service Worker: Installed')


    event.waitUntil(
        caches
            .open(cacheName)
            .then((cache) => {
                console.log('Service Worker: Caching Files')
                cache.addAll(cachedAssets);
            })
            .then(() => self.skipWaiting())
    );
});

//activate
self.addEventListener('activate', event => {
    console.log('Service Worker: Activated');
});

self.addEventListener('fetch', event => {
    console.log('Service Worker: Fetching');

    event.respondWith(fetch(event.request)
        .catch(() => {
            return caches.match(event.request)
        })
    )
})


/*for notification*/

self.addEventListener('push', event => {
    const data = event.data.json();
    self.registration.showNotification(data.title, {
        body: data.body,
        icon: 'android-chrome-192.png',
    });
});


// In your service_worker_cached.js
self.addEventListener('fetch', (event) => {
    const url = new URL(event.request.url);

    // Bypass Service Worker for all API POST requests
    if (url.pathname.includes('/api/') && event.request.method === 'POST') {
        event.respondWith(fetch(event.request));
        return;
    }

    // Your existing caching logic for other requests
    event.respondWith(
        caches.match(event.request).then(response => {
            return response || fetch(event.request);
        })
    );
});
