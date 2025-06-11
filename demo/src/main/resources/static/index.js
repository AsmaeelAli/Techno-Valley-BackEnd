document.addEventListener('DOMContentLoaded', function () {
    const postInput = document.getElementById("postInput");
    const postButton = document.getElementById("postButton");
    const postsContainer = document.getElementById("postsContainer");
    const imageUpload = document.getElementById("imageUpload");
    const fileUpload = document.getElementById("fileUpload");
    const searchInput = document.getElementById("searchInput");

    if (!postInput || !postButton || !postsContainer || !imageUpload || !fileUpload || !searchInput) {
        console.error("Some required elements are missing in the HTML.");
        return;
    }

    let selectedImage = null;
    let selectedFile = null;
    let isPosting = false;

    const token = window.JWT.getToken();

    // Debounce utility
    function debounce(fn, delay) {
        let timer;
        return function (...args) {
            clearTimeout(timer);
            timer = setTimeout(() => fn.apply(this, args), delay);
        };
    }

    // البحث مع Debounce
    searchInput.addEventListener('input', debounce(async (e) => {
        const query = e.target.value.trim();
        if (query.length < 2) return;

        try {
            // طلب البحث باستخدام AJAX
            const response = await fetch(`/api/search?q=${encodeURIComponent(query)}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const suggestions = await response.json();
                displaySearchSuggestions(suggestions);
            } else {
                const err = await response.text();
                alert(`خطأ في البحث: ${err}`);
            }
        } catch (error) {
            alert('فشل في جلب الاقتراحات. تحقق من الاتصال.');
        }
    }, 400));

    function displaySearchSuggestions(result) {
        const suggestionBox = document.getElementById('suggestionBox');
        suggestionBox.innerHTML = '';

        if ((!result.posts || result.posts.length === 0) && (!result.experts || result.experts.length === 0)) {
            const noResultsItem = document.createElement('div');
            noResultsItem.textContent = 'No results found';
            noResultsItem.className = 'suggestion-item no-results';
            suggestionBox.appendChild(noResultsItem);
            return;
        }

        if (result.posts) {
            result.posts.forEach(post => {
                const item = document.createElement('div');
                item.textContent = `Post : ${post.tag}`;
                item.className = 'suggestion-item';
                suggestionBox.appendChild(item);
            });
        }

        if (result.experts) {
            result.experts.forEach(expert => {
                const item = document.createElement('div');
                item.textContent = `User : ${expert.username}`;
                item.className = 'suggestion-item';
                suggestionBox.appendChild(item);
            });
        }
    }

    function dataURLtoFile(dataurl, filename) {
        let arr = dataurl.split(','),
            mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]),
            n = bstr.length,
            u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new File([u8arr], filename, { type: mime });
    }

    imageUpload.addEventListener("change", (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => { selectedImage = e.target.result; };
            reader.readAsDataURL(file);
        }
    });

    fileUpload.addEventListener("change", (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => { selectedFile = e.target.result; };
            reader.readAsDataURL(file);
        }
    });

    postButton.addEventListener('click', async () => {
        if (isPosting) return;

        let postContent = postInput.value.trim();

        const hashtagsArray = [];
        const hashtagRegex = /#([^\s#]+)/g;
        let match;
        while ((match = hashtagRegex.exec(postContent)) !== null) {
            hashtagsArray.push(match[1]);
        }
        postContent = postContent.replace(hashtagRegex, '').trim();
        const validHashtags = hashtagsArray.filter(tag => /[\w\u0600-\u06FF]+/.test(tag));

        if (!postContent) return alert('يرجى كتابة محتوى المنشور!');
        if (validHashtags.length === 0) return alert('يرجى إضافة هاشتاق واحد على الأقل يحتوي على كلمات أو أرقام!');

        const hashtags = validHashtags.join(' ');

        const postData = JSON.stringify({
            content: postContent,
            hashtag: hashtags
        });

        const formData = new FormData();
        formData.append('data', postData);

        if (selectedImage) formData.append('file', dataURLtoFile(selectedImage, 'image.png'));
        if (selectedFile) formData.append('file', dataURLtoFile(selectedFile, 'file.pdf'));

        isPosting = true;
        postButton.textContent = 'جاري النشر...';
        postButton.disabled = true;

        try {
            // إرسال طلب النشر باستخدام AJAX
            const response = await fetch(`/api/posts/create`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            const result = await response.json();

            if (!response.ok) {
                const msg = result.message || 'حدث خطأ أثناء النشر.';
                throw new Error(msg);
            }

            alert('تم نشر المنشور بنجاح!');
            postInput.value = '';
            imageUpload.value = '';
            fileUpload.value = '';
            selectedImage = null;
            selectedFile = null;

            fetchPosts();
        } catch (error) {
            alert(`خطأ: ${error.message}`);
        } finally {
            isPosting = false;
            postButton.textContent = 'نشر';
            postButton.disabled = false;
        }
    });

    async function fetchPosts() {
        try {
            const response = await fetch(`/api/posts/all`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) {
                postsContainer.innerHTML = "<p>لا توجد منشورات لعرضها.</p>";
                return;
            }

            const posts = await response.json();
            displayFilteredPosts(posts);
        } catch (error) {
            postsContainer.innerHTML = "<p>حدث خطأ أثناء تحميل المنشورات.</p>";
        }
    }

    // وظيفة لعرض المنشورات مع حالة الإعجاب والحفظ بناءً على البيانات المستلمة
    function displayFilteredPosts(posts) {
        postsContainer.innerHTML = '';

        if (posts.length === 0) {
            postsContainer.innerHTML = "<p>لا توجد منشورات لعرضها.</p>";
            return;
        }

        posts.reverse().forEach(post => {
            const postElement = document.createElement('div');
            postElement.classList.add('feed');

            const isLiked = post.isLikedByCurrentUser;
            const isSaved = post.isSavedByCurrentUser;
            const numOfLikes = post.numperOfLikes;

            postElement.innerHTML = `
                <div class="head">
                    <div class="user">
                        <div class="profile-photo" style="width: 50px; height: 50px; border-radius: 50%; background: #ccc;"></div>
                        <div class="info">
                            <h3>${post.username}</h3>
                            <small>${post.university}</small>
                        </div>
                    </div>
                    <span class="edit"><i class="uil uil-ellipsis-h"></i></span>
                </div>

                <div class="caption">
                    <p>${post.content}</p>
                    <div class="hashtags" style="color: #3498db;">${post.hashtag || ''}</div>
                </div>

                <div class="post-media">
                    ${post.imageUrl ? `<img src="${post.imageUrl}" class="post-image">` : ""}
                    ${post.fileUrl ? `<a href="${post.fileUrl}" target="_blank" class="file-link">تحميل الملف</a>` : ""}
                </div>

                <div class="action-buttons">
                    <button class="uil uil-heart ${post.isLiked ? 'active-like' : ''}" 
                            title="Like" 
                            onclick="toggleLike(this, '${post.id}')">
                    </button>
                    <span class="likes-count">${numOfLikes}</span>

                    <button class="uil uil-bookmark-full ${post.isSaved ? 'active-save' : ''}" 
                            title="Save" 
                            onclick="toggleSave(this, '${post.id}')">
                    </button>
                </div>
            `;

            postsContainer.appendChild(postElement);
        });
    }

    // وظيفة تغيير الإعجاب باستخدام AJAX
    window.toggleLike = async function (button, postId) {
        if (!postId) {
            alert('مفقود معرف المنشور');
            return;
        }

        const isActive = button.classList.contains('active-like');

        try {
            // إرسال طلب AJAX باستخدام fetch
            const response = await fetch(`/api/likes/toggle?postId=${postId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                // تحديث واجهة المستخدم بعد تغيير الحالة
                if (isActive) {
                    button.classList.remove('active-like');
                } else {
                    button.classList.add('active-like');
                }

                // تحديث عدد الإعجابات في الواجهة
                const postElement = button.closest('.feed');
                const likesCountElement = postElement.querySelector('.likes-count');
                let currentLikes = parseInt(likesCountElement.textContent);
                likesCountElement.textContent = isActive ? currentLikes - 1 : currentLikes + 1;
            } else {
                const msg = await response.text();
                alert(`حدث خطأ أثناء تحديث الإعجاب: ${msg}`);
            }
        } catch (error) {
            alert(`حدث خطأ: ${error.message}`);
        }
    };

    // وظيفة الحفظ باستخدام AJAX
    window.toggleSave = async function (button, postId) {
        if (!postId) {
            alert('مفقود معرف المنشور');
            return;
        }

        const isActive = button.classList.contains('active-save');

        try {
            // إرسال طلب AJAX باستخدام fetch
            const response = await fetch(`/api/saves/toggle?postId=${postId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                // تحديث واجهة المستخدم بعد تغيير الحالة
                if (isActive) {
                    button.classList.remove('active-save');
                } else {
                    button.classList.add('active-save');
                }
            } else {
                const msg = await response.text();
                alert(`حدث خطأ أثناء تحديث الحفظ: ${msg}`);
            }
        } catch (error) {
            alert(`حدث خطأ: ${error.message}`);
        }
    };

    fetchPosts();
});
