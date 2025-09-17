# React Coding Conventions

This document outlines the coding conventions for React projects to ensure consistency, readability, and maintainability across the codebase.

---

## Table of Contents

1.  [General Principles](#1-general-principles)
2.  [Components](#2-components)
    *   [Naming](#21-naming)
    *   [File Structure](#22-file-structure)
    *   [Props](#23-props)
    *   [State](#24-state)
    *   [Lifecycle Methods](#25-lifecycle-methods)
    *   [Functional Components and Hooks](#26-functional-components-and-hooks)
    *   [JSX](#27-jsx)
3.  [Styling](#3-styling)
4.  [JavaScript/TypeScript](#4-javascripttypescript)
5.  [Folder Structure](#5-folder-structure)
6.  [Performance](#6-performance)
7.  [Accessibility](#7-accessibility)
8.  [Testing](#8-testing)
9.  [Tooling](#9-tooling)

---

## 1. General Principles

*   **Consistency:** Adhere to these conventions to maintain a uniform codebase.
*   **Readability:** Write code that is easy to understand for anyone reading it.
*   **Maintainability:** Design components and modules to be easily updated and extended.
*   **Simplicity:** Prefer simple solutions over complex ones.
*   **Performance:** Be mindful of performance implications, especially for large applications.
*   **Accessibility:** Ensure all components are accessible to users with disabilities.

---

## 2. Components

### 2.1 Naming

*   **PascalCase for Component Names:** Always use PascalCase for React component names.
    ```jsx
    // Good
    function LoginForm() { /* ... */ }
    class UserProfile extends React.Component { /* ... */ }

    // Bad
    function loginForm() { /* ... */ }
    class userProfile extends React.Component { /* ... */ }
    ```
*   **camelCase for Prop Names:** Use camelCase for prop names.
    ```jsx
    // Good
    <UserCard userName="John Doe" profilePictureUrl="..."/>

    // Bad
    <UserCard user_name="John Doe" profilepictureurl="..."/>
    ```
*   **Component File Names:** Match the component name to the file name.
    ```
    // Good
    src/
    ├── components/
    │   ├── Button/
    │   │   └── Button.jsx
    │   └── Modal/
    │       └── Modal.jsx
    ```

### 2.2 File Structure

*   **One Component Per File:** Generally, keep one React component per file.
*   **Index Files for Export:** Use `index.js` (or `index.ts/tsx`) files for exporting components from a directory, especially when a component has sub-components or related files.
    ```
    src/
    ├── components/
    │   └── Button/
    │       ├── Button.jsx
    │       ├── Button.module.css
    │       └── index.js // exports { Button }
    ```

### 2.3 Props

*   **Destructuring Props:** Destructure props at the beginning of the functional component or in the `render` method of a class component.
    ```jsx
    // Functional Component
    function Button({ type, onClick, children }) {
      return <button type={type} onClick={onClick}>{children}</button>;
    }

    // Class Component
    class Button extends React.Component {
      render() {
        const { type, onClick, children } = this.props;
        return <button type={type} onClick={onClick}>{children}</button>;
      }
    }
    ```
*   **Prop Types/TypeScript:** Always define `propTypes` or use TypeScript interfaces for components to ensure type safety and documentation.
    ```jsx
    import PropTypes from 'prop-types';

    function MyComponent({ name, age }) { /* ... */ }

    MyComponent.propTypes = {
      name: PropTypes.string.isRequired,
      age: PropTypes.number,
    };
    ```
    ```typescript
    interface MyComponentProps {
      name: string;
      age?: number;
    }

    const MyComponent: React.FC<MyComponentProps> = ({ name, age }) => { /* ... */ };
    ```
*   **Default Props:** Use `defaultProps` for optional props to provide fallback values.
    ```jsx
    function MyComponent({ theme }) { /* ... */ }

    MyComponent.defaultProps = {
      theme: 'light',
    };
    ```
*   **Boolean Props:** For boolean props, when `true`, you can omit the value.
    ```jsx
    // Good
    <MenuItem isDisabled />

    // Bad
    <MenuItem isDisabled={true} />
    ```

### 2.4 State (Class Components)

*   **Initialize State in Constructor:** Initialize `this.state` in the constructor.
    ```jsx
    class Counter extends React.Component {
      constructor(props) {
        super(props);
        this.state = {
          count: 0,
        };
      }
      // ...
    }
    ```
*   **Use `setState` Function Form:** When `setState` depends on the previous state, use the functional form.
    ```jsx
    this.setState((prevState) => ({
      count: prevState.count + 1,
    }));
    ```
*   **Avoid Direct State Mutation:** Never directly modify `this.state`. Always use `this.setState()`.
    ```jsx
    // Bad
    this.state.count = 1;

    // Good
    this.setState({ count: 1 });
    ```

### 2.5 Lifecycle Methods (Class Components)

*   **Order of Lifecycle Methods:** Follow a consistent order for lifecycle methods (e.g., `constructor`, static `getDerivedStateFromProps`, `render`, `componentDidMount`, `componentDidUpdate`, `componentWillUnmount`, etc.).

### 2.6 Functional Components and Hooks

*   **Prefer Functional Components:** Whenever possible, prefer functional components with React Hooks over class components.
*   **`useState` for State:** Use `useState` for managing component-level state.
    ```jsx
    const [count, setCount] = useState(0);
    ```
*   **`useEffect` for Side Effects:** Use `useEffect` for side effects (data fetching, subscriptions, manual DOM manipulations).
    *   **Cleanup Function:** Always return a cleanup function from `useEffect` for subscriptions or listeners.
    ```jsx
    useEffect(() => {
      const timer = setInterval(() => {
        // ...
      }, 1000);
      return () => clearInterval(timer);
    }, []);
    ```
*   **`useContext` for Context:** Use `useContext` for consuming React Context.
*   **`useCallback` and `useMemo` for Optimization:** Use `useCallback` for memoizing functions and `useMemo` for memoizing expensive calculations to prevent unnecessary re-renders. Use them judiciously.
*   **Custom Hooks:** Extract reusable logic into custom hooks.
    ```jsx
    function useFetch(url) {
      const [data, setData] = useState(null);
      useEffect(() => {
        fetch(url).then(res => res.json()).then(setData);
      }, [url]);
      return data;
    }
    ```

### 2.7 JSX

*   **Self-Closing Tags:** Use self-closing tags for components that don't have children.
    ```jsx
    // Good
    <MyComponent />

    // Bad
    <MyComponent></MyComponent>
    ```
*   **Parentheses for Multiline JSX:** Wrap multiline JSX in parentheses.
    ```jsx
    // Good
    return (
      <div>
        <h1>Hello</h1>
        <p>World</p>
      </div>
    );

    // Bad
    return <div>
      <h1>Hello</h1>
      <p>World</p>
    </div>;
    ```
*   **Conditional Rendering:**
    *   **Short-circuit evaluation (&&):** For rendering a component conditionally if a condition is true.
        ```jsx
        {isLoading && <Spinner />}
        ```
    *   **Ternary operator (?:):** For `if-else` conditions.
        ```jsx
        {isLoggedIn ? <UserProfile /> : <LoginForm />}
        ```
*   **Lists and Keys:** Always provide a `key` prop when rendering lists of elements. The `key` should be stable, unique, and not derived from array index if the order can change.
    ```jsx
    {items.map(item => (
      <ListItem key={item.id} item={item} />
    ))}
    ```
*   **Spread Attributes:** Use sparingly and thoughtfully. Be explicit about props.
    ```jsx
    // Consider this
    <Button type="submit" onClick={handleSubmit} label="Submit" />

    // Instead of this, if props are numerous and not all directly relevant
    const buttonProps = { type: "submit", onClick: handleSubmit, label: "Submit" };
    <Button {...buttonProps} />
    ```

---

## 3. Styling

*   **CSS Modules:** Prefer CSS Modules for component-scoped styling to prevent global name collisions.
    ```jsx
    import styles from './Button.module.css';

    function Button() {
      return <button className={styles.button}>Click Me</button>;
    }
    ```
*   **Styled Components / Emotion:** Use CSS-in-JS libraries like Styled Components or Emotion for dynamic styling and theming, especially in larger applications.
    ```jsx
    import styled from 'styled-components';

    const StyledButton = styled.button`
      background-color: ${props => (props.primary ? 'blue' : 'gray')};
      color: white;
      padding: 10px 20px;
      border-radius: 5px;
    `;

    function MyButton({ primary }) {
      return <StyledButton primary={primary}>My Button</StyledButton>;
    }
    ```
*   **Utility Classes (Tailwind CSS):** Consider utility-first CSS frameworks like Tailwind CSS for rapid UI development and highly consistent design systems.
*   **Avoid Inline Styles:** Generally, avoid inline styles as they make it harder to manage styles and don't support pseudo-selectors or media queries. Use `style` prop only for truly dynamic and specific styles.
    ```jsx
    // Bad
    <div style={{ backgroundColor: 'red', fontSize: '16px' }}>Hello</div>

    // Good (if truly dynamic)
    <div style={{ backgroundColor: isActive ? 'blue' : 'gray' }}>Hello</div>
    ```

---

## 4. JavaScript/TypeScript

*   **ES6+ Features:** Embrace modern JavaScript features (const/let, arrow functions, destructuring, spread/rest operators, template literals).
*   **Arrow Functions:** Prefer arrow functions for component methods and callbacks to maintain `this` context automatically, especially in class components.
    ```jsx
    // Good (in class component)
    handleClick = () => {
      this.setState({ clicked: true });
    };

    // Good (in functional component)
    const handleClick = () => { /* ... */ };
    ```
*   **Import Order:** Establish a consistent import order (e.g., external libraries, internal components, utilities, styles).
    ```javascript
    // 1. React and Libraries
    import React, { useState, useEffect } from 'react';
    import PropTypes from 'prop-types';
    import { useParams } from 'react-router-dom';

    // 2. Local Components
    import Button from '../Button';
    import Card from '../../Card';

    // 3. Utilities and Helpers
    import { formatCurrency } from '../../utils/helpers';
    import config from '../../config';

    // 4. Styles
    import styles from './ProductDetail.module.css';
    ```
*   **TypeScript:** If using TypeScript, ensure strict type checking, define clear interfaces/types, and leverage its features for better code quality and developer experience.

---

## 5. Folder Structure

*   **Feature-Based:** Organize files by feature or domain rather than type.
    ```
    src/
    ├── features/
    │   ├── Auth/
    │   │   ├── components/
    │   │   │   ├── LoginForm.jsx
    │   │   │   └── SignupForm.jsx
    │   │   ├── hooks/
    │   │   │   └── useAuth.js
    │   │   ├── services/
    │   │   │   └── authService.js
    │   │   └── pages/
    │   │       └── LoginPage.jsx
    │   ├── Products/
    │   │   ├── components/
    │   │   │   ├── ProductCard.jsx
    │   │   │   └── ProductList.jsx
    │   │   ├── hooks/
    │   │   │   └── useProducts.js
    │   │   ├── services/
    │   │   │   └── productService.js
    │   │   └── pages/
    │   │       └── ProductsPage.jsx
    ├── components/ // General-purpose, reusable components (e.g., Button, Modal, Spinner)
    │   ├── Button/
    │   │   └── Button.jsx
    │   └── Layout/
    │       └── Layout.jsx
    ├── hooks/ // Global hooks
    ├── utils/ // Utility functions
    ├── services/ // Global API services
    ├── contexts/ // Global Contexts
    ├── assets/
    │   ├── images/
    │   └── icons/
    └── App.jsx
    └── index.js
    ```
*   **Atomic Design:** For very large projects, consider Atomic Design principles (Atoms, Molecules, Organisms, Templates, Pages).
    *   **Atoms:** Smallest UI elements (e.g., Button, Input, Icon).
    *   **Molecules:** Groups of atoms functioning as a unit (e.g., SearchBar, LoginForm).
    *   **Organisms:** Groups of molecules and atoms forming distinct sections (e.g., Header, ProductGrid).
    *   **Templates:** Page-level objects that place organisms into a layout.
    *   **Pages:** Specific instances of templates with real content.

---

## 6. Performance

*   **Memoization (`React.memo`, `useCallback`, `useMemo`):** Use these to prevent unnecessary re-renders, but profile first. Don't over-optimize.
*   **Lazy Loading (`React.lazy`, `Suspense`):** Use for code splitting and loading components only when needed, especially for routes or large components.
    ```jsx
    const MyLazyComponent = React.lazy(() => import('./MyLazyComponent'));

    function App() {
      return (
        <React.Suspense fallback={<div>Loading...</div>}>
          <MyLazyComponent />
        </React.Suspense>
      );
    }
    ```
*   **Virtualization:** For large lists, use libraries like `react-window` or `react-virtualized` to render only the visible items.
*   **Avoid Inline Object Creation:** Be mindful of creating new object/array literals or functions directly in JSX props that are passed to memoized components, as this will bypass memoization.
    ```jsx
    // Bad (if MyComponent is memoized)
    <MyComponent data={{ id: 1 }} onClick={() => console.log('clicked')} />

    // Good (use useState/useRef or define outside render)
    const data = useMemo(() => ({ id: 1 }), []);
    const handleClick = useCallback(() => console.log('clicked'), []);
    <MyComponent data={data} onClick={handleClick} />
    ```

---

## 7. Accessibility

*   **Semantic HTML:** Use semantic HTML elements (e.g., `<button>`, `<input>`, `<h1>`, `<ul>`) as much as possible.
*   **ARIA Attributes:** Use appropriate WAI-ARIA roles, states, and properties when semantic HTML isn't sufficient (e.g., `aria-label`, `role="dialog"`).
*   **Keyboard Navigation:** Ensure all interactive elements are keyboard navigable and focusable.
*   **Form Labels:** Always associate labels with form controls using the `htmlFor` prop.
    ```jsx
    <label htmlFor="username">Username</label>
    <input id="username" type="text" />
    ```
*   **Alt Text for Images:** Provide meaningful `alt` text for all `<img>` tags.
    ```jsx
    <img src="logo.png" alt="Company Logo" />
    ```
*   **Linter (`eslint-plugin-jsx-a11y`):** Integrate `eslint-plugin-jsx-a11y` to catch common accessibility issues during development.

---

