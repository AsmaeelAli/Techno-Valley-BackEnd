document.addEventListener("DOMContentLoaded", () => {
    const menuItems = document.querySelectorAll('.menu-item');
    const currentPage = window.location.pathname.split("/").pop(); // مثل: info.html

    menuItems.forEach(item => {
        const href = item.getAttribute("href");

        if (href === currentPage) {
            item.classList.add("active");

            // Optional: bounce animation
            item.animate([
                { transform: 'scale(1)' },
                { transform: 'scale(1.05)' },
                { transform: 'scale(1)' }
            ], {
                duration: 200,
                easing: 'ease-out'
            });
        }

        item.addEventListener("click", () => {
            menuItems.forEach(el => el.classList.remove("active"));
            item.classList.add("active");
        });
    });
});

function handleLogout() {
    alert("Logging out...");
}
