
import React, { useContext, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FiSearch, FiShoppingBag, FiMenu, FiX } from "react-icons/fi";
import AppContext from "../Context/Context";
import API from "../axios";
import "./Prasora.css";

const Navbar = ({ onSelectCategory }) => {
  const { cart } = useContext(AppContext);
  const navigate = useNavigate();

  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const [message, setMessage] = useState("");

  const categories = [
    "Laptop",
    "Headphone",
    "Mobile",
    "Electronics",
    "Toys",
    "Fashion",
  ];

  const handleSearch = async (e) => {
    e.preventDefault();

    if (!input.trim()) return;

    setLoading(true);
    setMessage("");

    try {
      const response = await API.get(
        `/products/search?keyword=${encodeURIComponent(input.trim())}`
      );

      if (response.data.length === 0) {
        setMessage("No products found. Try another search.");
      } else {
        navigate("/search-results", {
          state: { searchData: response.data },
        });
        setInput("");
        setMenuOpen(false);
      }
    } catch (error) {
      console.error("Search error:", error);
      setMessage("Unable to search products. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const selectCategory = (category) => {
    onSelectCategory(category);
    navigate("/");
    setMenuOpen(false);
  };

  return (
    <header className="prasora-header">
      <div className="prasora-topbar">
        <div className="prasora-nav-container">
          <Link to="/" className="prasora-logo">
            PRASORA<span>.</span>
          </Link>

          <form className="prasora-search" onSubmit={handleSearch}>
            <FiSearch size={19} />
            <input
              type="search"
              placeholder="Search for products..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
            />
            <button type="submit" disabled={loading}>
              {loading ? "Searching..." : "Search"}
            </button>
          </form>

          <div className="prasora-nav-actions">
            <Link to="/orders" className="prasora-nav-link">
              Orders
            </Link>

            <Link to="/cart" className="prasora-cart-link">
              <FiShoppingBag size={21} />
              <span>Bag</span>
              <span className="prasora-cart-count">{cart.length}</span>
            </Link>

            <button
              className="prasora-menu-toggle"
              onClick={() => setMenuOpen(!menuOpen)}
              aria-label="Toggle navigation"
            >
              {menuOpen ? <FiX /> : <FiMenu />}
            </button>
          </div>
        </div>
      </div>

      <nav className={`prasora-category-nav ${menuOpen ? "open" : ""}`}>
        <div className="prasora-nav-container prasora-category-inner">
          <Link to="/" onClick={() => setMenuOpen(false)}>
            Discover
          </Link>

          {categories.map((category) => (
            <button
              key={category}
              onClick={() => selectCategory(category)}
            >
              {category}
            </button>
          ))}

          <Link
            to="/add_product"
            onClick={() => setMenuOpen(false)}
          >
            Admin
          </Link>
        </div>
      </nav>

      {message && (
        <div className="prasora-search-message">
          {message}
          <button onClick={() => setMessage("")}>×</button>
        </div>
      )}
    </header>
  );
};

export default Navbar;
