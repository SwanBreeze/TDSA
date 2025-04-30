
// Apply dark mode early based on localStorage
const storedTheme = localStorage.getItem('theme');
if (storedTheme === 'dark') {
    document.body.classList.add('dark');
}
function initializeSidebar() {
    // Toggle Sidebar
    const toggle = document.querySelector('.toggle');
    const sidebar = document.querySelector('.sidebar');
    
    if (toggle && sidebar) {
        toggle.addEventListener('click', function() {
            sidebar.classList.toggle('close');
        });

        // Dark/Light Mode Toggle
        const modeSwitch = document.querySelector('.toggle-switch');
        const modeText = document.querySelector('.mode-text');
        const moonIcon = document.querySelector('.moon');
        const sunIcon = document.querySelector('.sun');
        
        if (modeSwitch) {
            modeSwitch.addEventListener('click', function() {
                document.body.classList.toggle('dark');
                
                const isDark = document.body.classList.contains('dark');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');

    if (isDark) {
        modeText.innerText = 'Light Mode';
        moonIcon.style.display = 'none';
        sunIcon.style.display = 'block';
    } else {
        modeText.innerText = 'Dark Mode';
        moonIcon.style.display = 'block';
        sunIcon.style.display = 'none';
    }
});
        }
    }
}

// Initialize when DOM is loaded (for index.html)
document.addEventListener('DOMContentLoaded', initializeSidebar);

// Export for dynamic loading (for searchBar.html)
if (typeof window !== 'undefined') {
    window.initializeSidebar = initializeSidebar;
}


/*service worker section*/

// register
if('serviceWorker' in navigator)
{
    window.addEventListener('load',()=> {
        navigator.serviceWorker
        .register('/service_worker_cached.js')
        .then(reg => console.log('Service Worker: Registered'))
        .catch(err=> console.log(`Service Worker: Error: ${err}`))
    })
}
