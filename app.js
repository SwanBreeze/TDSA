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
                
                if(document.body.classList.contains('dark')) {
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