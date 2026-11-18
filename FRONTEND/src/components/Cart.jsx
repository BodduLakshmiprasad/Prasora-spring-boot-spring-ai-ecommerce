
import React, { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import AppContext from "../Context/Context";
import CheckoutPopup from "./CheckoutPopup";
import { Button } from "react-bootstrap";

const Cart = () => {
  const { cart, removeFromCart } = useContext(AppContext);

  const [cartItems, setCartItems] = useState([]);
  const [showModal, setShowModal] = useState(false);

  const baseUrl = import.meta.env.VITE_BASE_URL || "http://localhost:8081";

  useEffect(() => {
    setCartItems(cart);
  }, [cart]);

  const totalPrice = cartItems.reduce(
    (total, item) => total + Number(item.price) * item.quantity,
    0
  );

  const handleIncreaseQuantity = (itemId) => {
    setCartItems((previousItems) =>
      previousItems.map((item) =>
        item.id === itemId
          ? {
              ...item,
              quantity: item.quantity + 1
            }
          : item
      )
    );
  };

  const handleDecreaseQuantity = (itemId) => {
    setCartItems((previousItems) =>
      previousItems.map((item) =>
        item.id === itemId
          ? {
              ...item,
              quantity: Math.max(item.quantity - 1, 1)
            }
          : item
      )
    );
  };

  const handleRemoveFromCart = (itemId) => {
    removeFromCart(itemId);
    setCartItems((previousItems) =>
      previousItems.filter((item) => item.id !== itemId)
    );
  };

  return (
    <div className="container mt-5 pt-5">
      <div className="row justify-content-center">
        <div className="col-md-11">
          <div className="card shadow-sm">
            <div className="card-header bg-white py-3">
              <h4 className="mb-0">Shopping Cart</h4>
            </div>

            <div className="card-body">
              {cartItems.length === 0 ? (
                <div className="text-center py-5">
                  <i className="bi bi-cart-x fs-1 text-muted"></i>
                  <h5 className="mt-3">Your cart is empty</h5>
                  <Link to="/" className="btn btn-primary mt-3">
                    Continue Shopping
                  </Link>
                </div>
              ) : (
                <>
                  <div className="table-responsive">
                    <table className="table table-hover align-middle">
                      <thead>
                        <tr>
                          <th>Product</th>
                          <th>Price</th>
                          <th>Quantity</th>
                          <th>Total</th>
                          <th>Action</th>
                        </tr>
                      </thead>

                      <tbody>
                        {cartItems.map((item) => (
                          <tr key={item.id}>
                            <td>
                              <div className="d-flex align-items-center">
                                <img
                                  src={`${baseUrl}/products/${item.id}/image`}
                                  alt={item.name}
                                  className="rounded me-3"
                                  width="75"
                                  height="75"
                                  style={{ objectFit: "contain" }}
                                  onError={(e) => {
                                    e.currentTarget.style.display = "none";
                                  }}
                                />

                                <div>
                                  <h6 className="mb-0">{item.name}</h6>
                                  <small className="text-muted">
                                    {item.category || ""}
                                  </small>
                                </div>
                              </div>
                            </td>

                            <td>₹{Number(item.price).toFixed(2)}</td>

                            <td>
                              <div
                                className="input-group input-group-sm"
                                style={{ width: "120px" }}
                              >
                                <button
                                  className="btn btn-outline-secondary"
                                  onClick={() =>
                                    handleDecreaseQuantity(item.id)
                                  }
                                  disabled={item.quantity <= 1}
                                >
                                  -
                                </button>

                                <input
                                  type="text"
                                  className="form-control text-center"
                                  value={item.quantity}
                                  readOnly
                                />

                                <button
                                  className="btn btn-outline-secondary"
                                  onClick={() =>
                                    handleIncreaseQuantity(item.id)
                                  }
                                >
                                  +
                                </button>
                              </div>
                            </td>

                            <td className="fw-bold">
                              ₹
                              {(
                                Number(item.price) * item.quantity
                              ).toFixed(2)}
                            </td>

                            <td>
                              <button
                                className="btn btn-sm btn-outline-danger"
                                onClick={() =>
                                  handleRemoveFromCart(item.id)
                                }
                              >
                                <i className="bi bi-trash"></i>
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  <div className="card mt-3">
                    <div className="card-body d-flex justify-content-between">
                      <h5 className="mb-0">Total:</h5>
                      <h5 className="mb-0">
                        ₹{totalPrice.toFixed(2)}
                      </h5>
                    </div>
                  </div>

                  <div className="d-grid mt-4">
                    <Button
                      variant="primary"
                      size="lg"
                      onClick={() => setShowModal(true)}
                    >
                      Proceed to Checkout
                    </Button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      </div>

      <CheckoutPopup
        show={showModal}
        handleClose={() => setShowModal(false)}
        cartItems={cartItems}
        totalPrice={totalPrice}
      />
    </div>
  );
};

export default Cart;
