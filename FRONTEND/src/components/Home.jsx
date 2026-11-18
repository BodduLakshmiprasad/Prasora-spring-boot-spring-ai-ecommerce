
import React, { useContext } from "react";
import { Link } from "react-router-dom";
import { FiArrowRight, FiShoppingCart } from "react-icons/fi";
import { toast } from "react-toastify";
import AppContext from "../Context/Context";
import unplugged from "../assets/unplugged.png";
import "./Prasora.css";

const Home = ({ selectedCategory }) => {
  const { data, isError, addToCart } = useContext(AppContext);

  const filteredProducts = selectedCategory
    ? data.filter((product) => product.category === selectedCategory)
    : data;

  const getImage = (product) => {
    const image = product.imageData;

    if (!image) return unplugged;
    if (image.startsWith("data:") || image.startsWith("http")) {
      return image;
    }

    return `data:${product.imageType || "image/jpeg"};base64,${image}`;
  };

  const handleAddToCart = (product) => {
    if (product.productAvailable === false || product.stockQuantity === 0) {
      return;
    }

    addToCart(product);
    toast.success(`${product.name} added to your bag!`);
  };

  if (isError) {
    return (
      <div className="prasora-empty">
        <h2>Something went wrong</h2>
        <p>We couldn't load our products. Please try again.</p>
      </div>
    );
  }

  return (
    <main className="prasora-home">

      {!selectedCategory && (
        <section className="prasora-hero">
          <div className="prasora-hero-content">
            <span className="prasora-eyebrow">WELCOME TO PRASORA</span>

            <h1>
              Find what you love.
              <br />
              <span>Love what you find.</span>
            </h1>

            <p>
              Explore a collection of products chosen to make
              everyday shopping a better experience.
            </p>

            <a href="#products" className="prasora-hero-button">
              Explore collection <FiArrowRight />
            </a>
          </div>

          <div className="prasora-hero-art">
            <div className="prasora-hero-circle">
              <span>PRASORA</span>
              <FiShoppingCart size={78} />
              <small>YOUR EVERYDAY DISCOVERY</small>
            </div>
          </div>
        </section>
      )}

      <section className="prasora-products-section" id="products">
        <div className="prasora-section-heading">
          <div>
            <span className="prasora-eyebrow">
              {selectedCategory ? "EXPLORE CATEGORY" : "OUR COLLECTION"}
            </span>
            <h2>
              {selectedCategory || "Discover our products"}
            </h2>
            <p>Find something you'll love.</p>
          </div>

          <span className="prasora-product-count">
            {filteredProducts.length} products
          </span>
        </div>

        {filteredProducts.length === 0 ? (
          <div className="prasora-empty">
            <h3>No products available</h3>
            <p>We're still adding products to this collection.</p>
            {selectedCategory && (
              <Link to="/" className="prasora-outline-button">
                View all products
              </Link>
            )}
          </div>
        ) : (
          <div className="prasora-product-grid">
            {filteredProducts.map((product) => {
              const unavailable =
                product.productAvailable === false ||
                product.stockQuantity === 0;

              return (
                <article className="prasora-product-card" key={product.id}>
                  <Link
                    to={`/product/${product.id}`}
                    className="prasora-product-image"
                  >
                    <img
                      src={getImage(product)}
                      alt={product.name}
                      onError={(e) => {
                        e.currentTarget.src = unplugged;
                      }}
                    />

                    {unavailable && (
                      <span className="prasora-stock-label">
                        Out of stock
                      </span>
                    )}
                  </Link>

                  <div className="prasora-product-info">
                    <span className="prasora-product-category">
                      {product.category || "Collection"}
                    </span>

                    <Link
                      to={`/product/${product.id}`}
                      className="prasora-product-name"
                    >
                      {product.name}
                    </Link>

                    {product.description && (
                      <p className="prasora-product-description">
                        {product.description}
                      </p>
                    )}

                    <div className="prasora-product-bottom">
                      <span className="prasora-price">
                        ₹{Number(product.price).toLocaleString("en-IN")}
                      </span>

                      <button
                        className="prasora-add-button"
                        onClick={() => handleAddToCart(product)}
                        disabled={unavailable}
                        aria-label={`Add ${product.name} to cart`}
                      >
                        <FiShoppingCart />
                      </button>
                    </div>
                  </div>
                </article>
              );
            })}
          </div>
        )}
      </section>

      <footer className="prasora-footer">
        <Link to="/" className="prasora-logo">
          PRASORA<span>.</span>
        </Link>
        <p>Discover. Explore. Shop.</p>
        <small>© {new Date().getFullYear()} PRASORA. All rights reserved.</small>
      </footer>

    </main>
  );
};

export default Home;
