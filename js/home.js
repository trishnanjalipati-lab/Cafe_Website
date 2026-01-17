document.addEventListener("DOMContentLoaded", () => {
  const searchInput = document.getElementById("searchInput");
  const menuItems = document.querySelectorAll(".menu-item");

  searchInput.addEventListener("input", () => {
    const query = searchInput.value.toLowerCase();

    menuItems.forEach(item => {
      const text = item.textContent.toLowerCase();

      if (text.includes(query)) {
        item.style.display = "list-item";
      } else {
        item.style.display = "none";
      }
    });
  });
});
